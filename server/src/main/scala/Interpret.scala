package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import com.twitter.util._
import scala.io.Source

object Interpret extends cycle.Plan with cycle.SynchronousExecution with ServerErrorResponse {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case GET(Path("/scala")) => 
			logger.debug("GET /scala")
			view(Map.empty)(<p></p>)
		case POST(Path("/scala") & Params(params)) =>
			logger.debug("POST /scala")
			val vw = view(params)_
			vw(<p></p>)
	}
	
	def view(params: Map[String, Seq[String]])(body: scala.xml.NodeSeq) = {
		def content(k: String) = params.get(k).flatMap { _.headOption } getOrElse("")
		Html(
			<html>
			<head>
			    <title>Scala Worksheet</title>
			    <script src="js/codemirror.js"></script>
			    <script src="js/clike.js"></script>
			    <script src="js/css.js"></script>
			    <script src="js/jquery.js"></script>
			    <link rel="stylesheet" href="css/codemirror.css"/>
			    <link rel="stylesheet" href="css/eclipse.css"/>
			    <link rel="stylesheet" href="css/docs.css"/>
			</head>
			<body>
			    <h1>Live Development Environment</h1>
			    { body }
			<form method="POST">
			<textarea id="code" name="code" class="CodeMirror">{content("code")}</textarea>
			</form>
			
			<textarea id="interpreter" name="interpreter" class="interpreter" readonly="readonly">{content("interpreter")}</textarea>
			
			</body>
			</html>
				)
  }
}
