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
	val results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	var ids = Seq[String]()
	
	val intpCfg = MyInterpreter.Config()
	intpCfg.out = Some(output)
	
	val interpreter = MyInterpreter(intpCfg)
	Misc.injectConversions(interpreter)
	
    
	def intent = {
		case GET(Path("/scala")) =>
			Redirect("/editor")
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			
			evaluateCode(data("code").head)
			
			EditorView.data("code") = data("code")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
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
	def evaluateCode(code : String) = {
			val splited = code.split("\n")
			splited.foreach { command =>
				val res = interpreter.interpret(command)
				res match {
					case Success( name, value ) => {
						ids = ids :+ name
						val res1 = interpreter.interpret(name + ".toHtml")
						res1 match {
							case Success( name1, value1 ) => EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n" + value1 + "\n")
							case _ => EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n" + value + "\n\nscala> ")
						}
					}
					case _ => EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n" + res.toString + "\n\nscala> ")
				}
			}
	   }
}
