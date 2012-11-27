package pt.feup.lde

import scala.xml._
import scala.collection.mutable._
import scala.util.Random
import java.io._

import pt.feup.lde.Utilities._
import pt.feup.lde.Utilities.Misc._
import pt.feup.lde.MyInterpreter._
import unfiltered.Cookie

object Evaluation {
	
	val iterableSlicing = 13;
	val results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	var ids = Map.empty[ String, String ]
	var interpreterCounter = 0
	var interpreterID = ""
	var special = false;
	//var interpreters = Seq[MyInterpreter]()
	//var interpretersConfigs = Seq[Config]()
	val interpreters = HashMap.empty[String,MyInterpreter]
	 
	 
	def evaluatePartial(code : String,interpreterId : String) : String = {
		val lines = code.split(":!:")
		val interpreter = interpreters(interpreterId)
		val instruction = "val tmp_var_" + Math.abs(Random.nextInt()) + " = " + lines.apply(0).trim + "." + ( if(lines.apply(1).trim.equals("toHtml")) ("slice(0," + iterableSlicing + ").toHtml") else lines.apply(1).trim )
		println("Partial evaluation requested. Submited instruction: " + instruction + "\n")
		interpreter.interpret(instruction,true) match {
			case Success(name1,value1) => {
				println("\nSucces! converted partially and got for " + lines.apply(0) + " = " + value1 + "\n")
				value1.toString
			}
			case _ => ""
		}
	}



	def evaluateSingle(code : String,interpreterId : String) : String = {
		
		var resultString : String = ""
		
		val lines = code.split(":!:")
		var firstName : String = ""
		var lastName : String = ""
		
		val interpreter = interpreters(interpreterId)
		
		results.getBuffer().setLength(0)
		
		val res = interpreter.interpret(lines apply(0))
		res match {
			case Success( auxName, null ) => {
				println("\nENTREI NO SUCCESS VALUE NULL\n")
				resultString = composeHtmlResult(code, auxName, "()", false)
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
				special = false
				interpreter.interpret("val tmp_var_" + Math.abs(Random.nextInt()) + " = manOf(" + firstName + ").erasure.toString.split(' ').apply(1).trim",true) match {
					case Success(auxName3,auxValue3) => {
						println("FROM ERASURE : " + auxName3 + " -> " + auxValue3 + "\n")
						
						if(auxValue3.toString().contains("scala.collection.")){
							interpreter.interpret("val tmp_var_" + Math.abs(Random.nextInt()) + " = " + firstName + ".slice(0," + iterableSlicing + ")",true) match {
								case Success(auxName4,auxResult4) => lastName = auxName4
								case _ => lastName = firstName
							}
						}
						else {
							auxValue3 match {
								case "scala.lang.String" | "String" => {
									special = true
								}
								case _ => {
									println("GOT OTHER TYPE. IGNORING.")
									lastName = firstName
								}
							}
						}
					}
					case _ => lastName = firstName
				}
				
				
				if(value!=null) {
					interpreter.interpret("val tmp_var_" + Math.abs(Random.nextInt()) + " = " + lastName + "." + ( if(lines.length > 1) lines apply(1) trim else "toHtml" ),true) match {
						case Success( auxName2, auxValue2 ) => {
							println("Success converting " + firstName + ". " + auxName2 + " = " + auxValue2 toString)
							resultString = composeHtmlResult(code, firstName, auxValue2 toString, special)
						}
						case _ => {
							println("No conversion for " + firstName + ". Value is " + value toString)
							resultString = composeHtmlResult(code, firstName, value toString, special)
						}
					}
				}
			}
			case Incomplete => {
				println("\nENTREI NO INCOMPLETE!!!\n")
				resultString = composeFailedEvaluation(false,"")
			}
			case _ => {
				println("\nENTREI NO DEFAULT\n")
				resultString = composeFailedEvaluation(true,results.toString)
			}
		}
		//extractIds(results toString)
		println("\n\n ========== OUTPUT =============\n" + results.toString + "\n ============================== \n")
		//EditorView.data("interpreter") = EditorView.data("interpreter") :+ resultString
		resultString
	}
	
	def getInterpreterID(cookies : Option[Cookie]) : String = {
		
		cookies match {
				case Some(Cookie("session",session,_,_,_,_,_,_)) => {
					println("\n\nGOT COOKIES! VALUE IS " + session.toString + "\n")
					session.toString
				}
				case _ => {
					println("\n\nDIDNT HAVE COOKIES ON POST REQUEST! SOMETHING WENT VERY WRONG\n")
					interpreters.keySet.head.toString
				}
		}
	}
	
	def setUpInterpreter(cookies : Option[Cookie]) : String = {
		 cookies match {
				case Some(Cookie(_,session,_,_,_,_,_,_)) => {
					println("\n\nGOT COOKIES! VALUE IS '" + session.toString + "'\n")
					if(interpreters.keySet.contains(session)){
						println("Key is valid")
						session.toString
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
						injectConversions(interpreter)
						println("Done. Interpreter (" + interpreterID + ") is ready!\nWaiting for requests...")
						interpreterID
					}
					
					//EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",interpreterID))
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
					injectConversions(interpreter)
					println("Done. Interpreter (" + interpreterID + ") is ready!\nWaiting for requests...")
					interpreterID
					//EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",interpreterID))
				}
			}
		}
	 
	 /**
	  * injectConversions
	  * This function will inject the Conversions object
	  * onto the MyInterpreter object
	  * loading the Conversions from the Resources/scala folder
	  */
	  def injectConversions(i : MyInterpreter) : Boolean = {
		  //still missing catching the exception from readFromFile...
		  val code : String = readFromFile(getClass,"/scala/Conversions.scala")
		  i.interpret(code,true)
		  //i.compileString(code)
		  true
	  }
	  
     /**
	   * composeHtmlResult and composeFailedEvaluation
	   * 
	   * composeHtmlResult receives the input code, the name of the identifier, the value of the result and a value determining if there is an HTML or Literal String to be displayed
	   * and returns a string representing a DOM element for the obtained result.
	   * 
	   * composeFailedEvaluation receives a Boolean representing an error message or not and a message containing (if any) the error message
	   * and returns a string representing a DOM element containing the aler message for the specific case (error or incomplete instruction)
	   * 
	   */ 
	  def composeHtmlResult( code: String, name: String, value: String, string: Boolean) : String = { 
		"<!doctype html>" +
		"<div id='#div_" + name + "'>" +
		"<div style='display:inline;'>" + 
		"<span id='#label_" + name + "' class='label labelInput' data-toggle='collapse' data-target='#" + name + "'><big>" + name + "</big>: " + ( if(code.length>150) (fromHtmltoString(code.substring(0,150) + "...")) else fromHtmltoString(code) ) + "</span>" + 
		"<span class='dropdown-span' data-dropdown='#dropdown-" + name + "'>View as</span>" + 
		"</div>"+
		"<div id='dropdown-" + name + "' class='dropdown-menu'>" + 
			"<ul>" +  
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"val tmp_var_" + Math.abs(Random.nextInt()) + " = " + name + " :!: toD3BarChart\");'>Bar Chart</a></li>" + 
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"val tmp_var_" + Math.abs(Random.nextInt()) + " = " + name + " :!: toHeapTree\");'>Heap Tree</a></li>" + 
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"val tmp_var_" + Math.abs(Random.nextInt()) + " = " + name + " :!: toHtml\");'>Html</a></li>" + 
			"</ul>" +  
		"</div>" + 
		"<div id='" + name + "' class='collapse in'>" + 
		"<div id='well_" + name + "' class='well well-small'>" + ( if(string) fromHtmltoString(value) else value ) +
		"</div></div></div>"
	}
	
	def composeFailedEvaluation( error: Boolean , msg: String) : String = {
		println("fromstringtohtml:\n" + fromStringtoHtml(msg))
		"<!doctype html>" +
		"<div class='alert" + ( if(error) " alert-error" else "" ) + "'>" +
		"<button type='button' class='close' data-dismiss='alert'>x</button>" +
		"<strong>" + ( if(error) "Error!" else "Warning!" ) + "</strong><br>" +
		( if(error) fromStringtoHtml(msg) else "Your instruction was incomplete!" ) +
		"</div>"
	}
}
