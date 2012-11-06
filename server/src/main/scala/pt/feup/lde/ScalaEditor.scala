package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import collection.mutable._
import java.io._

import pt.feup.lde.MyInterpreter._
import pt.feup.lde.Misc._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	var results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	
	var ids = Map.empty[ String, String ]
	var i_counter = 0
	
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
				println("\n\nI'm going to interpret " + data("code").head)
				ResponseString(evaluateSingle(data("code").head))
			}
			else {
				println("Got data with 0 length. Showing view with same data.\n\n")
				EditorView.view(EditorView.data)(NodeSeq.Empty)
			}
	}
	

	def evaluateSingle(code : String) : String = {
		
		i_counter = i_counter + 1
		
		var resultString : String = ""
		
		val res = interpreter.interpret(code)
		res match {
			case Success( name, value ) => {
				
				extractIds(results.toString)
				
				val res1 = interpreter.interpret(name + ".toHtml",true)
				res1 match {
					case Success( name1, value1 ) => resultString = composeHtmlResult(code, name, value1.toString)
					case _ => resultString = composeHtmlResult(code, name, value.toString)
				}
			}
			case Error( _ ) => resultString = composeFailedEvaluation(true)
			case Incomplete => resultString = composeFailedEvaluation(false)
		}
		EditorView.data("interpreter") = EditorView.data("interpreter") :+ resultString
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
