package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import de.sciss.scalainterpreter._

import collection.mutable._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	
	val intpCfg = MyInterpreter.Config()
	val interpreter = MyInterpreter(intpCfg)
	
	def intent = {
		case GET(Path("/scala")) =>
			Redirect("/editor")
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			//Eval.compile("object Session { }")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			//val splited = data("code").head.split("\n")
			//splited.foreach { command =>
				//val res = interpreter.interpret(command)
				//println(res.toString)
				//EditorView.data("interpreter") = EditorView.data("interpreter") :+ res.toString
			//}
			
			val res = interpreter.interpret(data("code").head)
			
			EditorView.data("code") = data("code")
			EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n\nscala> " + res)
			EditorView.view(EditorView.data)(NodeSeq.Empty)
	}
}
