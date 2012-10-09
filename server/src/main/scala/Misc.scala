package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import java.io.FileNotFoundException

/**
 *	Server traits definitions 
 * 	Different traits defined in order to be used with any Handler
 *  In their own files, an handler for the Server must extends one of
 *  the following traits
 * 
 *  Added MemoryExecutor in order to limit memory usage to prevent failures
 * 
 */
trait ServerPlan1 extends cycle.Plan with cycle.ThreadPool with ServerErrorResponse

trait ServerPlan2 extends cycle.Plan with cycle.DeferralExecutor with cycle.DeferredIntent with ServerErrorResponse {
	def underlying = MemoryExecutor.underlying
}

object MemoryExecutor {
	import org.jboss.netty.handler.execution._
	lazy val underlying = new MemoryAwareThreadPoolExecutor(
		16, 65536, 1048576)
}

/**
 * Miscellaneous object containing utility functions
 * 
 * readFromFile : reads and returns the contents of a file from the resource path
 * 
 */
object Misc {
	def readFromFile(klass: Class[_], path: String) : String = {
		klass.getResourceAsStream(path) match {
			case null =>
				throw new FileNotFoundException(path)
			case stream =>
		        io.Source.fromInputStream(stream).getLines().mkString("\n")
		}
	}
}

/**
 *  User-defined views for HTML
 *  On any handler one can use any of the views defined below
 *  
 *  Every view object should contain a map named data as well as
 *  methods content and view
 */ 

object EditorView {
	
	var data = scala.collection.mutable.Map("code" -> Seq("val keywords = List(\"Apple\", \"Ananas\", \"Mango\", \"Banana\", \"Beer\")\nval result = keywords.sorted.groupBy(_.head)\nprintln(result)"), "interpreter" -> Seq("\n\n\nscala> "))
	
	def content(k: String) = data.get(k).flatMap { _.headOption } getOrElse("")
	
	def view(data: scala.collection.mutable.Map[String, Seq[String]])(body: NodeSeq) = {
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
<input type="submit"/>
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

object REPLView {
	
	def view(data: scala.collection.mutable.Map[String, Seq[String]])(body: NodeSeq) = {
		Html(<html><head></head><body></body></html>)
	}
}
