package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import collection.mutable._
import java.io._

import pt.feup.lde.MyInterpreter._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	import Misc._
	
	val logger = org.clapper.avsl.Logger(getClass)
	val results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	
	var ids = Map.empty[ String, String ]
	
	println("Setting up Interpreter's configuration...")
	val intpCfg = MyInterpreter.Config()
	intpCfg.out = Some(results)
	
	println("Creating a new Intepreter instance...")
	val interpreter = MyInterpreter(intpCfg)
	

	println("Reading and loading Conversions onto the Interpreter...")
	Misc.injectConversions(interpreter)
	println("\nDone. Interpreter is ready!\nWaiting for requests...")
    
	def intent = {
		case GET(Path("/scala")) =>
			Redirect("/repl")
		
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			Redirect("/repl")
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			Redirect("/repl")
			
		case GET(Path("/repl")) =>
			logger.debug("GET /repl")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/repl") & Params(data)) =>
		
			//println("Data: " + data)
			//println("Code: " + data("code"))
			
			if(data("code").length > 0) {
				//println("\n\nI'm going to interpret " + data("code").head)
				ResponseString(evaluateSingle(data("code").head,"toD3BarChart"))
			}
			else {
				println("Got data with 0 length. Showing view with same data.\n\n")
				EditorView.view(EditorView.data)(NodeSeq.Empty)
			}
	}
	

	def evaluateSingle(code : String, conversion: String) : String = {
		
		var resultString : String = ""
		
		val lines = code.split(":!:")
		var firstName : String = ""
		var lastName : String = ""
		
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
				interpreter.interpret(firstName + ".isInstanceOf[Iterable[Any]]",true) match {
					case Success(auxName1, auxResult1) => {
						if(auxResult1.asInstanceOf[Boolean]) {
							interpreter.interpret(firstName + ".slice(0,10)",true) match {
								case Success(auxName2,auxResult2) => lastName = auxName2
								case _ => lastName = firstName
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
