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
				val args = data("code").head.split(":!:")
				
				if(args.length == 2){
					
					if(args.apply(0).trim.equals("conversions")) {
						//TEST IF IT IS TO RESPOND WITH CONVERSION OR TO REVERT TO DEFAULT
						args.apply(1) match {
							case x if(x.trim.startsWith("reload-")) => {
								val file = x.slice(x.indexOf("-")+1,x.length)
								ResponseString(Evaluation.injectConversionFileCookie(cookies("session"),file) toString)
							}
							case x => {
								ResponseString(Evaluation.getConversionsFile(x.trim))
							}
						}
					}
					else {
						ResponseString(Evaluation.evaluateConversion(args.apply(0),Evaluation.getInterpreterID(cookies("session"))) toString)
					}
				}
				else {
					if(args.length > 2 && args.apply(2).trim.equals("partial"))
						ResponseString(Evaluation.evaluatePartial(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
					else ResponseString(Evaluation.evaluateSingle(data("code").head,Evaluation.getInterpreterID(cookies("session"))) toString)
				}
			}
			else {
				println("Got data with 0 length. Showing view with same data.\n\n")
				ResponseString("")
			}
	}
}
