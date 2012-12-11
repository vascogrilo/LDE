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
			
			println("RECEIVED: " + data("code") + "\n\n")
			
			if(data("code").length > 0) {
				//println("\n\nI'm going to interpret " + data("code").head)
				val args = data("code").head.split(":!:")
				
				if(args.length > 1 && args.apply(0).trim.equals("conversions") && args.apply(1).trim.equals("conv"))
					ResponseString(Evaluation.getConversionsCode)
				else {
					if(args.length > 2 && args.apply(2).trim.equals("partial"))
						ResponseString(Evaluation.evaluatePartial(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
					else ResponseString(Evaluation.evaluateSingle(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
			}
			else {
				println("Got data with 0 length. Showing view with same data.\n\n")
				//EditorView.view(EditorView.data)(NodeSeq.Empty)
				ResponseString("")
			}
	}
}
