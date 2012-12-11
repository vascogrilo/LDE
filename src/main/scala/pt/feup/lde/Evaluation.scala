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
	var instructionCounter = 0
	
	val converts = scala.collection.mutable.Map.empty[Int,Map[String,String]]
	
	val interpreters = HashMap.empty[String,MyInterpreter]
	
	/**
	 * initConversionsMenu
	 * 
	 * This method aims to initialize the Map containing the List of conversions available for
	 * a certain category (represented by an integer)
	 * 
	 */
    def initConversionsMenu = {
		converts += (0 -> Map.empty[String,String])
		converts += (1 -> Map("toBinaryTree" -> "Binary Tree",
								"toHtml" -> "Paginated List",
								"toHtmlList" -> "HTML List",
								"toD3BarChart" -> "Bar Chart",
								"toString" -> "Text"))
		converts += (2 -> Map("toPieChart" -> "Pie Chart",
								"toHtml" -> "Html Table",
								"toString" -> "Text"))
	}
	
	initConversionsMenu
	
	/**
	 * evaluatePartial
	 * 
	 * This method aims to interpret an instruction.
	 * It is only called for conversions, and it doesn't generates a full response because we want to append the result to an existing result
	 * 
	 */
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


	/**
	 * evaluateSingle
	 * 
	 * This method aims to interpret an instruction.
	 * 
	 * It is called when the user submits an instruction to be evaluated.
	 * It interprets the code and generates a full response to be added to the Front-End
	 * 
	 * It comprises 3 different phases on evaluation.
	 * Phase 1 for interpreting the original instruction.
	 * Phase 2 for pre-conversion additional operations depending on the Type of the result from Phase 1
	 * Phase 3 for requesting a conversion
	 * 
	 * Finally, return the full response with the resulting value
	 * 
	 */
	def evaluateSingle(code : String,interpreterId : String) : String = {
		
		var resultString : String = ""
		
		val lines = code.split(":!:")
		
		val interpreter = interpreters(interpreterId)
		
		results.getBuffer().setLength(0)
		
		println("Full response evaluation requested! Goint to interpret: " + lines.apply(0) + "\n")
		
		resultString = evaluationPhase1(interpreter,lines.apply(0),lines)
		println("\n\n ========== OUTPUT =============\n" + results.toString + "\n ============================== \n")
		resultString
	}
	
	
	/**
	 * evaluationPhase1
	 * 
	 * This method is part of a 3 step operation.
	 * 
	 * This phase 1 intends to interpret the original code submited by the user and if
	 * the operation is successfull it then calls for evaluationPhase2
	 * If the interpretation is not succesfull it returns a response with an error message.
	 * 
	 */
	def evaluationPhase1(interpreter : MyInterpreter, code : String, lines : Array[java.lang.String]) : String = {
		
		var firstName : String = ""
		var lastName : String = ""
		val res = interpreter.interpret(code)
		res match {
			case Success( auxName, () ) => {
				println("\nENTREI NO SUCCESS " + auxName + " COM VALUE ()\n")
				composeHtmlResult(code, Math.abs(Random.nextInt()).toString, "()", false, false, 0)
			}
			case Success( auxName, null) => {
				println("\n ENTREI NO VALUE NULL!!! O que esta no output: (" + results.toString + ")\n")
				composeHtmlResult(code, Math.abs(Random.nextInt()).toString, results.toString, false, false, 0)
			}
			case Success( auxName, value ) => {
				println("\nENTREI NO SUCCESS: " + auxName + " COM VALUE " + value.toString + "\n")
				firstName = auxName
				println("Success: " + firstName + " " + value toString)
				
				evaluationPhase2(interpreter, "val tmp_var_" + Math.abs(Random.nextInt()) + " = manOf(" + firstName + ").erasure.toString",code,firstName,lastName,value,lines)
			}
			case Incomplete => {
				println("\nENTREI NO INCOMPLETE!!!\n")
				composeFailedEvaluation(false,"")
			}
			case _ => {
				println("\nENTREI NO DEFAULT\n")
				composeFailedEvaluation(true,results.toString)
			}
		}
	}
	
	
	
	/**
	 * evaluationPhase2
	 * 
	 * This method is part of a 3 step operation.
	 * 
	 * There are some pre-conversion additional operations to be applied to some certains types.
	 * For example, slicing to a max limit an iterable for showing the paginated list.
	 * 
	 * This phase 2 intends to determine the value of the result from phase 1.
	 * If the type has operations to be made previously to its convertion this methods applies those operations before calling for evaluationPhase3.
	 * If not it immediately calls for evaluationPhase3
	 * 
	 */
	def evaluationPhase2(interpreter : MyInterpreter, code : String, originalCode : String, firstNameA: String, lastNameA: String, value: Any, lines : Array[java.lang.String]) : String = {
		
		/*
		 * DETECTION OF TYPES.
		 * IT MUST FIRST TEST THE TYPE/TRAIT INSPECTED TO SEE IF THERE ARE ADDITIONAL
		 * OPERATIONS THAT MUST BE DONE TO THE RESULT PREVIOUS TO RENDERING IT.
		 * LIKE SLICING AN ITERABLE FOR EXAMPLE.
		 * 
		 */
		println("PHASE 2:\n TAMANHO DO LINES: " + lines.length + "\n CODE TO BE INTERPRETED: " + code + "\n")
		var firstName = firstNameA
		var lastName = lastNameA
		var special = false
		var category = 0;
		interpreter.interpret(code,true) match {
			case Success(auxName3,auxValue3) => {
				println("FROM ERASURE : " + auxName3 + " -> " + auxValue3 + "\n")
				
				/*if(auxValue3.toString().contains("scala.collection.")){
					category = 1;
					interpreter.interpret("val tmp_var_" + Math.abs(Random.nextInt()) + " = " + firstName + ".slice(0," + iterableSlicing + ")",true) match {
						case Success(auxName4,auxResult4) => lastName = auxName4
						case _ => lastName = firstName
					}
				}
				else {
					auxValue3 match {
						case "java.lang.String" | "String" => {
							special = true
						}
						case _ => {
							println("GOT OTHER TYPE. IGNORING.")
						}
					}
					lastName = firstName
				}*/
				auxValue3.toString match {
					case x:String if(x.contains("Map")) => {
						category = 2;
						lastName = firstName
					}
					case x:String if(x.contains("scala.collection")) => {
						category = 1;
						interpreter.interpret("val tmp_var_" + Math.abs(Random.nextInt()) + " = " + firstName + ".slice(0," + iterableSlicing + ")",true) match {
							case Success(auxName4,auxResult4) => lastName = auxName4
							case _ => lastName = firstName
						}
					}
					case x:String if(x.contains("java.lang.String")) => {
						special = true
						lastName = firstName
					}
					case _ => {
						lastName = firstName
					}
				}
			}
			case _ => lastName = firstName
		}
		
		evaluationPhase3(interpreter,"val tmp_var_" + Math.abs(Random.nextInt()) + " = " + lastName + "." + ( if(lines.length > 1) (lines apply(1) trim) else "toHtml" ),originalCode,firstName,value,special,lines,category)
	}
	
	
	
	/**
	 * evaluationPhase3
	 * 
	 * This method is part of a 3 step operation.
	 * 
	 * This phase intends to interpret the convertion toHtml to the result from interpreting the original code submited by the user.
	 * It then returns a response containing the result from the conversion.
	 */
	def evaluationPhase3(interpreter : MyInterpreter, code : String, originalCode: String,firstName: String, value: Any,special: Boolean, lines : Array[java.lang.String], category: Int) : String = {
		
		interpreter.interpret(code,true) match {
			case Success( auxName2, auxValue2 ) => {
				println("Success converting " + firstName + ". " + auxName2 + " = " + auxValue2 toString)
				composeHtmlResult(originalCode, firstName, auxValue2 toString, special, true, category)
			}
			case _ => {
				println("No conversion for " + firstName + ". Value is " + value toString)
				composeHtmlResult(originalCode, firstName, value toString, special, true, category)
			}
		}
	}
	
	
	/**
	 * initInterpreter
	 * 
	 * This method sets up a new Interpreter instance.
	 * It starts by generating a new key for indexing it and stores it on the interpreters Map
	 * 
	 * It returns the generated key for future referencing.
	 */
	def initInterpreter : String = {
		val interpreterId = Random.nextDouble().toString
		println("New key generated. Value is '" + interpreterId + "'\n")
		println("Setting up Interpreter's (" + interpreterId + ") configuration...")
		val intpCfg = MyInterpreter.Config()
		intpCfg.out = Some(results)
		
		println("Creating a new Intepreter (" + interpreterId + ") instance...")
		val interpreter = MyInterpreter(intpCfg)
		
		interpreters += (interpreterId -> interpreter)
	
		println("Reading and loading Conversions onto the Interpreter (" + interpreterId + ") ...")
		injectConversions(interpreter)
		println("Done. Interpreter (" + interpreterId + ") is ready!\nWaiting for requests...")
		interpreterId
	}
	
	
	/**
	 * getInterpreterID
	 * 
	 * This method receives a cookie from the Server handler ScalaEditor and tests whether it contains a session key or not.
	 * 
	 * Since this method is called on POST requests it is expected that a session key exists otherwise something went very wrong.
	 * It it does, it returns its value.
	 * 
	 * If it doens't exist it returns a valid key from the interpreters Map
	 * 
	 */
	def getInterpreterID(cookies : Option[Cookie]) : String = {
		
		cookies match {
				case Some(Cookie("session",session,_,_,_,_,_,_)) => {
					println("\n\nGOT COOKIES! VALUE IS " + session.toString + "\n")
					if(interpreters.contains(session.toString))
						session.toString
					else ""
				}
				case _ => {
					println("\n\nDIDNT HAVE COOKIES ON POST REQUEST! SOMETHING WENT VERY WRONG\n")
					interpreters.keySet.head.toString
				}
		}
	}
	
	
	/**
	 * setUpInterpreter
	 * 
	 * This method receives a cookie from the Server handler ScalaEditor and tests whether it contains a session key or not.
	 * The session key represents the key of the interpreter's instance stored on the interpreters Map.
	 * 
	 * If the session key exists and it's a valid one it returns it.
	 * If it exists but it's not a valid key (doesn't exist on the Map) it calls for initInterpreter.
	 * If it doesn't exist it calls for initInterpreter
	 * 
	 */
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
						initInterpreter
					}
				}
				case _ => {
					println("\n\nDIDNT HAVE COOKIES! GENERATING NEW INTERPRETER AND SETTING COOKIE TO INTERPRETER ID\n")
					initInterpreter
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
		  println("\n====================\n" + results.toString + "\n====================\n")
		  //i.compileString(code)
		  true
	  }
	  
	  
	  
     /**
	   * composeHtmlResult
	   * 
	   * composeHtmlResult receives the input code, the name of the identifier, the value of the result and a value determining if there is an HTML or Literal String to be displayed
	   * and returns a string representing a DOM element for the obtained result.
	   * 
	   */
	  def composeHtmlResult( code: String, name: String, value: String, string: Boolean, showName : Boolean, category: Int) : String = { 
		println("GOT IF IT IS STRING = " + string.toString)
		val temp = Math.abs(Random.nextInt())
		instructionCounter = instructionCounter + 1
		"<!doctype html>" +
		"<div id='div_" + name + "_TEMPORARYID" + instructionCounter + "'>" +
		"<div style='display:inline;'>" + 
		"<span id='label_" + name + "_TEMPORARYID" + instructionCounter + "' class='label labelInput' data-toggle='collapse' data-target='#" + name + "_TEMPORARYID" + instructionCounter + "'>" + 
		( if(showName) ("<big>" + name + "</big>: ") else "") + 
		( if(code.length>120) (fromHtmltoString(code.substring(0,120) + "...",new java.lang.StringBuilder())) else fromHtmltoString(code,new java.lang.StringBuilder()) ) + "</span>" + 
		{ category match {
				case 0 => ""
				case x => "<span class='dropdown-span' data-dropdown='#dropdown-"+name+"'>View as</span><div id='dropdown-"+name+"' class='dropdown-menu'>"+
							"<ul>"+
							{ converts(x).map( x1 => "<li><a href='javascript:void(0)' onclick='requestConversion(\""+name+"_TEMPORARYID"+instructionCounter+"\",\""+name+" :!: "+(x1._1)+"\");'>"+(x1._2)+"</a></li>" ) mkString("") }+
							"</ul></div>"
			}
		} +
		"</div>" + 
		"<div id='" + name + "_TEMPORARYID" + instructionCounter + "' class='collapse in'>" + 
		"<div id='well_" + name + "_TEMPORARYID" + instructionCounter + "' class='well well-small' style='overflow-x: auto;'>" + toInterpretableString(value) +
		"</div></div></div>"
	}
	
	
	
	 /**
	  * 
	  * composeFailedEvaluation 
	  * 
	  * composeFailedEvaluation receives a Boolean representing an error message or not and a message containing (if any) the error message
	  * and returns a string representing a DOM element containing the aler message for the specific case (error or incomplete instruction)
	  * 
	  */ 
	def composeFailedEvaluation( error: Boolean , msg: String) : String = {
		println("fromstringtohtml:\n" + fromStringtoHtml(msg, new java.lang.StringBuilder()))
		"<!doctype html>" +
		"<div class='alert" + ( if(error) " alert-error" else "" ) + "'>" +
		"<button type='button' class='close' data-dismiss='alert'>x</button>" +
		"<strong>" + ( if(error) "Error!" else "Warning!" ) + "</strong><br>" +
		( if(error) fromStringtoHtml(msg, new java.lang.StringBuilder()) else "Your instruction was incomplete!" ) +
		"</div>"
	}
}
