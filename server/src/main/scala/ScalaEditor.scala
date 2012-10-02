package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import com.twitter.util._
import scala.xml._

trait ServerPlan1 extends cycle.Plan with cycle.ThreadPool with ServerErrorResponse

trait ServerPlan2 extends cycle.Plan with cycle.DeferralExecutor with cycle.DeferredIntent with ServerErrorResponse {
	def underlying = MemoryExecutor.underlying
}

object MemoryExecutor {
	import org.jboss.netty.handler.execution._
	lazy val underlying = new MemoryAwareThreadPoolExecutor(
		16, 65536, 1048576)
}

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	var data = scala.collection.Map("code" -> List("val keywords = List(\"Apple\", \"Ananas\", \"Mango\", \"Banana\", \"Beer\")\nval result = keywords.sorted.groupBy(_.head)\nprintln(result)"), "interpreter" -> List("\n\n\nscala> "))
	
	def intent = {
		case GET(Path("/scala")) => 
			logger.debug("GET /scala")
			view(data)(NodeSeq.Empty)
		case POST(Path("/scala") & Params(data)) =>
			logger.debug("POST /scala")
			println("")
			println(content("code"))
			println("")
			val vw = view(data)_
			val result = (new Eval).apply[() => String](content("code").apply(0).toString)
			vw(<p> { result.toString } </p>)
	}
	
	def content(k: String) = data.get(k).flatMap { _.headOption } getOrElse("")
	
	def view(data: scala.collection.Map[String, Seq[String]])(body: NodeSeq) = {
		Html(
<html>
<head>
<meta charset="utf-8"/>
<title>Scala Worksheet</title>
<script src="js/codemirror.js"></script>
<script src="js/clike.js"></script>
<script src="js/css.js"></script>
<link rel="stylesheet" href="css/codemirror.css"/>
<link rel="stylesheet" href="css/eclipse.css"/>
<link rel="stylesheet" href="css/docs.css"/>
</head>
<body>
<h1>Live Development Environment</h1>
<h3>Scala Worksheet</h3>

<form method="POST">
<textarea id="code" name="code" class="CodeMirror">{ content("code") }</textarea>
<textarea id="interpreter" name="interpreter" class="interpreter" readonly="readonly">{ content("interpreter") }</textarea>
</form>
</body>

<script>
window.onload = styleEditor('code','eclipse');
</script>
</html>)
  }
}
