package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import com.twitter.util._
import scala.xml._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case GET(Path("/scala")) =>
			Redirect("/editor")
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			Eval.compile("object Session { }")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			val splited = EditorView.data("code").apply(0).split("\n")
			splited.foreach { command =>
				Eval.inPlace[Any](command)
			}
			val vw = EditorView.view(data)_
			vw(<p> { } </p>)
	}
}
