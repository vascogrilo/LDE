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
	val logger = org.clapper.avsl.Logger(getClass)
	var results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	var ids = Seq.empty[ String ]
	
	println("Setting up Interpreter's configuration...")
	val intpCfg = MyInterpreter.Config()
	//intpCfg.out = Some(results)
	
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
			//EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			Redirect("/repl")
			/*//interpreter.resetAndLoad
			evaluateAllCode(data("code").head)
			//Misc.printOutIds(results.toString)
			
			EditorView.data("code") = data("code")
			EditorView.view(EditorView.data)(NodeSeq.Empty)*/
			
		case GET(Path("/repl")) =>
			logger.debug("GET /repl")
			EditorView.view3(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/repl") & Params(data)) =>
		
			evaluateSingle(data("code").head)
			
			EditorView.data("code") = data("code")
			EditorView.view3(EditorView.data)(NodeSeq.Empty)
	}
	

	def evaluateSingle(code : String) = {
		
		var resultString : String = ""
		
		resultString = resultString + "<div><span class='label label-info labelInput'>Input</span><div class='well well-small'>" +
										code + "</div>"
		
		val res = interpreter.interpret(code)
		res match {
			case Success( name, value ) => {
				val res1 = interpreter.interpret(name + ".toHtml",true)
				res1 match {
					case Success( name1, value1 ) => resultString = resultString + "<span class='label label-success labelOutput'>Output</span><div class='well'>" + value1.toString + "</div></div>"
					case _ => resultString = resultString + "<span class='label label-warning labelOutput'>Output</span><div class='well'>" + value.toString + "</div></div>"
				}
			}
			case Error( _ ) => resultString = resultString + "<span class='label label-important labelOutput'>Output</span><div class='well'> There was an error in your code! </div></div>"
			case Incomplete => resultString = resultString + "<span class='label label-warning labelOutput'>Output</span><div class='well'> Incomplete instruction! </div></div>"
		}
		EditorView.data("interpreter") = EditorView.data("interpreter") :+ resultString
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
