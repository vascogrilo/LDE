package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._
import unfiltered.Cookie

import scala.xml._
import scala.collection.mutable._
import scala.util.Random
import java.io._

import pt.feup.lde.MyInterpreter._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	import Misc._
	
	val iterableSlicing = 13;
	val logger = org.clapper.avsl.Logger(getClass)
	val results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	var ids = Map.empty[ String, String ]
	var interpreterCounter = 0
	var interpreterID = ""
	//var interpreters = Seq[MyInterpreter]()
	//var interpretersConfigs = Seq[Config]()
	val interpreters = HashMap.empty[String,MyInterpreter]

	
    
	def intent = {
		case GET(Path("/")) =>
			Redirect("/repl")
			
		case GET(Path("/scala")) =>
			Redirect("/repl")
		
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			Redirect("/repl")
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			Redirect("/repl")
			
		case GET(Path("/repl") & Cookies(cookies)) =>
			logger.debug("GET /repl")
			cookies("session") match {
				case Some(Cookie(_,session,_,_,_,_,_,_)) => {
					println("\n\nGOT COOKIES! VALUE IS '" + session.toString + "'\n")
					if(interpreters.keySet.contains(session)){
						println("Key is valid")
						interpreterID = session
					}
					else {
						println("Key is invalid. Creating new one and generating new interpreter.")
						interpreterID = Random.nextDouble().toString
						println("New key generated. Value is '" + interpreterID + "'\n")
						println("Setting up Interpreter's (" + interpreterID + ") configuration...")
						val intpCfg = MyInterpreter.Config()
						intpCfg.out = Some(results)
						
						println("Creating a new Intepreter (" + interpreterID + ") instance...")
						val interpreter = MyInterpreter(intpCfg)
						
						interpreters += (interpreterID -> interpreter)
					
						println("Reading and loading Conversions onto the Interpreter (" + interpreterID + ") ...")
						Misc.injectConversions(interpreter)
						println("\nDone. Interpreter (" + interpreterID + ") is ready!\nWaiting for requests...")
					}
					
					EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",interpreterID))
				}
				case _ => {
					println("\n\nDIDNT HAVE COOKIES! GENERATING NEW INTERPRETER AND SETTING COOKIE TO INTERPRETER ID\n")
					interpreterID = Random.nextDouble().toString
					println("Setting up Interpreter's (" + interpreterID + ") configuration...")
					val intpCfg = MyInterpreter.Config()
					intpCfg.out = Some(results)
					
					println("Creating a new Intepreter (" + interpreterID + ") instance...")
					val interpreter = MyInterpreter(intpCfg)
					
					interpreters += (interpreterID -> interpreter)
				
					println("Reading and loading Conversions onto the Interpreter (" + interpreterID + ") ...")
					Misc.injectConversions(interpreter)
					println("\nDone. Interpreter (" + interpreterID + ") is ready!\nWaiting for requests...")
					
					EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",interpreterID))
				}
			}
			//EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",interpreterID))
		
		case GET(Path("/forget")) =>
			ResponseCookies(Cookie("session","")) ~> Redirect("/repl")
		
		case POST(Path("/repl") & Params(data) & Cookies(cookies)) =>
		
			cookies("session") match {
				case Some(Cookie("session",session,_,_,_,_,_,_)) => {
					println("\n\nGOT COOKIES! VALUE IS " + session.toString + "\n")
					interpreterID = session
				}
				case _ => {
					println("\n\nDIDNT HAVE COOKIES ON POST REQUEST! SOMETHING WENT VERY WRONG\n")
					interpreterID = interpreters.keySet.head
				}
			}
			
			val args = data("code").head.split(":!:")
			
			if(data("code").length > 0) {
				//println("\n\nI'm going to interpret " + data("code").head)
				if(args.length > 2 && args.apply(2).trim.equals("partial"))
					ResponseString(evaluatePartial(data("code").head,interpreterID))
				else ResponseString(evaluateSingle(data("code").head,interpreterID))
			}
			else {
				println("Got data with 0 length. Showing view with same data.\n\n")
				EditorView.view(EditorView.data)(NodeSeq.Empty)
			}
	}
	
	
	def evaluatePartial(code : String,interpreterId : String) : String = {
		
		val lines = code.split(":!:")
		val interpreter = interpreters(interpreterId)
		interpreter.interpret(lines apply(0),true) match {
			case Success(name,value) => {
				val instruction = name + "." + ( if(lines.apply(1).trim.equals("toHtml")) ("slice(0," + iterableSlicing + ").toHtml") else lines.apply(1).trim )
				interpreter.interpret(instruction,true) match {
					case Success(name1,value1) => value1.toString
					case _ => value.toString
				}
			}
			case Error(e) => composeFailedEvaluation(true)
			case Incomplete => composeFailedEvaluation(false)
			case _ => composeFailedEvaluation(true)
		}
	}

	def evaluateSingle(code : String,interpreterId : String) : String = {
		
		var resultString : String = ""
		
		val lines = code.split(":!:")
		var firstName : String = ""
		var lastName : String = ""
		
		val interpreter = interpreters(interpreterId)
		
		val res = interpreter.interpret(lines apply(0))
		res match {
			case Success( auxName, null ) => {
				println("\nENTREI NO SUCCESS VALUE NULL\n")
				resultString = composeHtmlResult(code, auxName, "()" )
			}
			case Success( auxName, value ) => {
				println("\nENTREI NO SUCCESS COM VALUE\n")
				firstName = auxName
				println("Success: " + firstName + " " + value toString)
				
				/*
				 * DETECTION OF TYPES.
				 * IT MUST FIRST TEST THE TYPE/TRAIT INSPECTED TO SEE IF THERE ARE ADDITIONAL
				 * OPERATIONS THAT MUST BE DONE TO THE RESULT PREVIOUS TO RENDERING IT.
				 * LIKE SLICING AN ITERABLE FOR EXAMPLE.
				 * 
				 */
				
				//STARTING WITH TESTING IT THE RESULT IS ITERABLE
				//If it is we start by truncating the result to only 10 items
				/*interpreter.interpret(firstName + ".asInstanceOf[AnyRef].isInstanceOf[Iterable[Any]]",true) match {
					case Success(auxName1, auxResult1) => {
						if(auxResult1.asInstanceOf[Boolean]) {
							interpreter.interpret(firstName + ".slice(0," + iterableSlicing + ")",true) match {
								case Success(auxName2,auxResult2) => lastName = auxName2
								case _ => lastName = firstName
							}
						}
					}
					case _ => lastName = firstName
				}*/
				
				interpreter.interpret("manOf(" + firstName + ").erasure.toString.split(' ').apply(1).trim",true) match {
					case Success(auxName3,auxValue3) => {
						println("FROM ERASURE : " + auxName3 + " -> " + auxValue3 + "\n")
						auxValue3 match {
								case "scala.collection.immutable.List" |
										"scala.collection.mutable.List" |
										"scala.collection.immutable.Range" |
										"scala.collection.immutable.Range.Inclusive" => {
									println("GOT LIST!!!!!! SLICING IT.")
									interpreter.interpret(firstName + ".slice(0," + iterableSlicing + ")",true) match {
										case Success(auxName4,auxResult4) => lastName = auxName4
										case _ => lastName = firstName
									}
								}
								case _ => {
									println("GOT OTHER TYPE. IGNORING.")
									lastName = firstName
								}
						}
						
						
					}
					case _ => lastName = firstName
				}
				
				
				if(value!=null) {
					interpreter.interpret(lastName + "." + ( if(lines.length > 1) lines apply(1) trim else "toHtml" ),true) match {
						case Success( auxName2, auxValue2 ) => {
							println("Success converting " + firstName + ". " + auxName2 + " = " + auxValue2 toString)
							resultString = composeHtmlResult(code, firstName, auxValue2 toString)
						}
						case _ => {
							println("No conversion for " + firstName + ". Value is " + value toString)
							resultString = composeHtmlResult(code, firstName, value toString)
						}
					}
				}
			}
			case Incomplete => {
				println("\nENTREI NO INCOMPLETE!!!\n")
				resultString = composeFailedEvaluation(false)
			}
			case _ => {
				println("\nENTREI NO DEFAULT\n")
				resultString = composeFailedEvaluation(true)
			}
		}
		//extractIds(results toString)
		println("\n\n" + results.toString + "\n\n")
		//EditorView.data("interpreter") = EditorView.data("interpreter") :+ resultString
		resultString
	}
	
	
	
	
	
	
	/**
	 * evaluateCode
	 * This function will iterate over every instuction on the parameter received
	 * and evaluate it one by one.
	 * It will inspect each result and (if successful add the new identifier to the list) and 
	 * try and call toHtml conversion on every result indentifier.
	 * If there is no toHtml conversion a verbose conversion will take place.
	 * 
	 * TEMPORARY: CONSTANTLY REFACTOR THIS AND IMPROVE IT
	 */
	
	/**
	 * 
	 * GARBAGE CODE
	 * 
	 * 
	def evaluateAllCode(code : String) = {
		EditorView.resetResultData
		results.getBuffer().setLength(0)
		
		val res = interpreter.interpret(code)
		
		var resultString : String = ""
		res match {
			case Success( name, value ) => {
				ids = Misc.extractIds(results.toString)
				ids.map { id => {
					
					val res1 = interpreter.interpret(id + ".toHtml")
					
					res1 match {
						case Success( name1, value1 ) => resultString = id + " = " + value1
						case _ => resultString = id + " = " + value
					}
					
					EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("<p>> " + resultString + "</p>")
				  }
				}
			}
			case _ => {
				println("ERROR OR INCOMPLETE")
				EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("<p>> There was an error in your code! </p>")
			}
		}
	}
	
	def evaluateSplitCode(code : String) = {
		val splited = code.split("\n")
		
		splited.foreach { command =>
			
			if(!(command.trim).isEmpty) {
				
				val res = interpreter.interpret(command)
				var resultString : String = ""
				
				res match {
					case Success( name, value ) => {
						
						ids = ids :+ name
						val res1 = interpreter.interpret(name + ".toHtml",true)
						
						res1 match {
							case Success( name1, value1 ) => resultString = name + " = " + value1
							case _ => resultString = name + " = " + value
						}
					}
					case Error( _ ) => resultString = "There was an error in: " + command
					case Incomplete => resultString = "Incomplete instruction: " + command
				}
				EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("<p>> " + resultString + "</p>")
			}
		}
	}*/
}
