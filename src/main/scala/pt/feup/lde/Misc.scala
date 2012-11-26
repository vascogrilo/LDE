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
	
	import scala.util.matching._
	
	val Id = new Regex("""(\w+):\s+(.*)\s+=\s+.*""")
	val Error = new Regex("""<console>:\d+:\s+error:\s+(.*)""")
	
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
	 
	 /**
	  * injectConversions
	  * This function will inject the Conversions object
	  * onto the MyInterpreter object
	  * loading the Conversions from the Resources/scala folder
	  */
	  def injectConversions(i : MyInterpreter) : Boolean = {
		  //still missing catching the exception from readFromFile...
		  val code : String = Misc.readFromFile(getClass,"/scala/Conversions.scala")
		  i.interpret(code,true)
		  //i.compileString(code)
		  true
	  }
	  
	  
	  /**
	   * extractIds
	   * 
	   * This function will extract the Identifier and Type value from the string provided
	   */ 
	  def extractIds(s : String) = {
		  
		  val lines = s.split("\n")
		  lines.foreach{ line => 
		    println("\nLINE TO BE PARSED: " + line)
			matchIdOrErrortoString(line) match {
				case Some((a,b)) => println("Got " + a + " " + b)
				case None => println("Got nothing")
			}
		  }
	  }
	  
	  def matchIdOrErrortoString(s : String) : Option[(String,String)] = {
		  s match {
			  case Id(a,b) => Some((a,b))
			  case Error(e) => Some(("Error",e))
			  case _ => None
		  }
	  }
	  
	  
	  /**
	   * composeHtmlResult and composeFailedEvaluation
	   * 
	   * composeHtmlResult receives the input code, the name of the identifier, the value of the result and the instruction counter
	   * and returns a string representing a DOM element for the obtained result.
	   * 
	   * composeFailedEvaluation receives a Boolean representing an error message or not
	   * and returns a string representing a DOM element containing the aler message for the specific case (error or incomplete instruction)
	   * 
	   */ 
	  def composeHtmlResult( code: String, name: String, value: String) : String = { 
		"<!doctype html>" +
		"<div id='#div_" + name + "'>" +
		"<div style='display:inline;'>" + 
		"<span id='#label_" + name + "' class='label labelInput' data-toggle='collapse' data-target='#" + name + "'><big>" + name + "</big>: " + code + "</span>" + 
		"<span class='dropdown-span' data-dropdown='#dropdown-" + name + "'>View as</span>" + 
		"</div>"+
		"<div id='dropdown-" + name + "' class='dropdown-menu'>" + 
			"<ul>" +  
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"" + name + " :!: toD3BarChart\");'>Bar Chart</a></li>" + 
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"" + name + " :!: toHeapTree\");'>Heap Tree</a></li>" + 
			"<li><a href='javascript:void(0)' onclick='requestConversion(\"" + name + "\",\"" + name + " :!: toHtml\");'>Html</a></li>" + 
			"</ul>" +  
		"</div>" + 
		"<div id='" + name + "' class='collapse in'>" + 
		"<div id='well_" + name + "' class='well well-small'>" + value +
		"</div></div></div>"
	}
	
	def composeFailedEvaluation( error: Boolean ) : String = {
		"<!doctype html>" +
		"<div class='alert" + ( if(error) " alert-error" else "" ) + "'>" +
		"<button type='button' class='close' data-dismiss='alert'>x</button>" +
		"<strong>" + ( if(error) "Error!" else "Warning!" ) + "</strong> " +
		( if(error) "There was an error in your code!" else "Your instruction was incomplete!" ) +
		"</div>"
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
	
	import Misc._
	
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

object REPLView {
	
	def view(data: scala.collection.mutable.Map[String, Seq[String]])(body: NodeSeq) = {
		Html(<html><head></head><body></body></html>)
	}
}
