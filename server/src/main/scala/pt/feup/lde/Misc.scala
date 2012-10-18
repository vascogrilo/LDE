package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import java.io.FileNotFoundException
import pt.feup.lde.MyInterpreter._

/**
 * Miscellaneous object containing utility functions
 * 
 */
object Misc {
	
	/**
	 * readFromFile : reads and returns the contents of a file from the resource path
	 *
	 */
	def readFromFile(klass: Class[_], path: String) : String = {
		klass.getResourceAsStream(path) match {
			case null =>
				throw new FileNotFoundException(path)
			case stream =>
		        io.Source.fromInputStream(stream).getLines().mkString("\n")
		}
	}
	
	/**
	 * concatList : concatenates a list of strings into a single string
	 * 
	 */
	 def concatList(l : Seq[String]) : String = l match {
		 case Nil => ""
		 case _ => l.head + concatList(l.tail)
	 }
	 
	 /**
	  * injectConversions
	  * This function will inject the Conversions object
	  * onto the MyInterpreter object
	  * loading the Conversions from the Resources/scala folder
	  */
	  def injectConversions(i : MyInterpreter) : Boolean = {
		  //still missing catching the exception from readFromFile...
		  val code : String = Misc.readFromFile(getClass,"/scala/Conversions.scala")
		  i.interpret(code)
		  true
	  }
	   
}

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
 *  User-defined views for HTML
 *  On any handler one can use any of the views defined below
 *  
 *  Every view object should contain a map named data as well as
 *  methods content and view
 */ 

object EditorView {
	
	val interpreter_head = "Welcome to Scala Worksheet Live Development Environment version 0.1.<p/>" +
							"Running under Scala version 2.9.2 (OpenJDK Server VM, Java 1.6.0_24).<p/>" +
							"Type in expressions and hit (Cmd/Ctrl)+S to have them evaluated.<p/>"
								
	var data = scala.collection.mutable.Map(
		"code" -> Seq("val l = List(1,2,3)\n\nval ll = List(\"Item 1\",\"Item 2\",\"Item 3\")"),
		"interpreter" -> Seq(interpreter_head))
								
	val html1 : String = Misc.readFromFile(getClass,"/html/part1.html")
	val html2 : String = Misc.readFromFile(getClass,"/html/part2.html")
	val html3 : String = Misc.readFromFile(getClass,"/html/part3.html")
	
	def content(k: String) = data.get(k).flatMap { _.headOption } getOrElse("")
	
	def view(data: scala.collection.mutable.Map[String, Seq[String]])(body: NodeSeq) = {
		Html(XML.loadString(html1 + data("code").head + html2 + Misc.concatList(data("interpreter")) + html3))
	}
  
}

object REPLView {
	
	def view(data: scala.collection.mutable.Map[String, Seq[String]])(body: NodeSeq) = {
		Html(<html><head></head><body></body></html>)
	}
}
