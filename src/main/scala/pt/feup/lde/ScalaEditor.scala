package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._
import unfiltered.Cookie

import scala.xml._

import pt.feup.lde.Utilities._
import pt.feup.lde.Evaluation._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	
	val logger = org.clapper.avsl.Logger(getClass)
	
    
	def intent = {
		case GET(Path("/")) =>
			Redirect("/repl")
			
		case GET(Path("/scala")) =>
			Redirect("/repl")
			
		case GET(Path("/forget")) =>
			ResponseCookies(Cookie("session","")) ~> Redirect("/repl")
			
		case GET(Path("/repl") & Cookies(cookies)) =>
			logger.debug("GET /repl")
			
			EditorView.view(EditorView.data)(NodeSeq.Empty) ~> ResponseCookies(Cookie("session",Evaluation.setUpInterpreter(cookies("session"))))
		
		case POST(Path("/repl") & Params(data) & Cookies(cookies)) =>
			
			//println("RECEIVED: " + data("code") + "\n\n")
			
			data("type").head match {
				case "eval" => {
					println("GOT TYPE EVAL!! \n\n")
					ResponseString(Evaluation.evaluateSingle(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
				case "partial" => {
					println("GOT TYPE PARTIAL!! \n\n")
					ResponseString(Evaluation.evaluatePartial(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
				case "req" => {
					println("GOT TYPE REQUEST!! \n\n")
					ResponseString(Evaluation.getConversionsFile(data("code").head))
				}
				case "update" => {
					println("GOT TYPE UPDATE!! \n\n")
					ResponseString(Evaluation.evaluateConversion(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
				case "revert" => {
					println("GOT TYPE REVERT!! \n\n")
					ResponseString(Evaluation.injectConversionFileCookie(cookies("session"),data("code").head) toString)
				}
				case "new" => {
					println("GOT TYPE NEW CONVERSION!! \n\n")
					ResponseString(Evaluation.evaluateConversion(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
			}
	}
}
