package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import scala.util.Random
import java.io.FileNotFoundException
import pt.feup.lde.MyInterpreter._


/**
 * Miscellaneous object containing utility functions
 * 
 */
object Utilities {
	
	
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
	  
	  
	  object Misc {
		  /**
			 * readFromFile : reads and returns the contents of a file from the resource path
			 *
			 */
			def readFromFile(klass: Class[_], path: String) : String = {
				klass.getResourceAsStream(path) match {
					case null =>
						//throw new FileNotFoundException(path)
						""
					case stream =>
				        io.Source.fromInputStream(stream,"utf-8").getLines().mkString("\n")
				}
			}
		  
		  def fromStringtoHtml(s : String) : String = {
			  s match {
				  case "" => ""
				  case _ => toHtmlInput(s.head) + fromStringtoHtml(s.tail)
			  }
		  }
		  
		  def toHtmlInput(c : Char) : String = {
			  c match {
				  case '<' => "&lt;"
				  case '>' => "&gt;"
				  case '&' => "&amp;"
				  case '\n' => "<br/>"
				  case ' ' => "&nbsp;"
				  case _ => c toString
			  }
		  }
		  
		  def fromHtmltoString(s : String) : String = {
			  s match {
				  case "" => ""
				  case _ => toHtmlDisplayed(s.head) + fromHtmltoString(s.tail)
			  }
		  }
		  
		  def toHtmlDisplayed(c : Char) : String = {
			  c match {
				  case '<' => "&lt;"
				  case '>' => "&gt;"
				  case _ => c toString
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
		
		import Utilities.Misc._
		
		val interpreter_head = "Welcome to Scala Worksheet Live Development Environment version 0.1.<p/>" +
								"Running under Scala version 2.9.2 (OpenJDK Server VM, Java 1.6.0_24).<p/>" +
								"Type in expressions and hit (Cmd/Ctrl)+S to have them evaluated.<p/>"
									
		var data = scala.collection.mutable.Map(
			"code" -> Seq(""),
			"interpreter" -> Seq(""))
									
		//val html4 : String = readFromFile(getClass,"/html/View3_part1.html")
		//val html5 : String = readFromFile(getClass,"/html/View3_part2.html")
		//val html6 : String = readFromFile(getClass,"/html/View3_part3.html")
		val replView = readFromFile(getClass,"/html/REPLView.html")
		
		def view(data: scala.collection.mutable.Map[ String, Seq[ String ] ])(body: NodeSeq) = {
			//Html(XML.loadString(html4 + data("interpreter").reduceLeft(_ + _) + html5 + data("code").head + html6))
			Html(XML.loadString(replView))
		}
		
		def resetResultData = { 
			data("interpreter") = Seq(interpreter_head)
		}
	  
	}

}
