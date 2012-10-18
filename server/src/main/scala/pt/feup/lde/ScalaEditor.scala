package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import scala.xml._
import collection.mutable._
import java.io._

import pt.feup.lde.MyInterpreter._

object ScalaEditor extends ServerPlan2 {
	
	import QParams._
	val logger = org.clapper.avsl.Logger(getClass)
	val results = new StringWriter()
	val output = new OutputStreamWriter(System.out)
	var ids = Seq[String]()
	
	//val lp   = LogPane().makeDefault()
	val intpCfg = MyInterpreter.Config()
	//intpCfg.imports :+= "pt.feup.lde.Conversions._" 
	intpCfg.out = Some(output)
	//intpCfg.out = Some(results)
	//intCfg.out = Some( lp.writer )
	
	val interpreter = MyInterpreter(intpCfg)
	interpreter.interpret("object Conversions {	implicit def fromList[A](l : List[A]) = new Object { def toHtml = <ul> { l.map(e => <li> { e } </li>) } </ul> toString }}")
    interpreter.interpret("import Conversions._")
    
	def intent = {
		case GET(Path("/scala")) =>
			Redirect("/editor")
		case GET(Path("/editor")) => 
			logger.debug("GET /editor")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
		case POST(Path("/editor") & Params(data)) =>
			logger.debug("POST /editor")
			val splited = data("code").head.split("\n")
			splited.foreach { command =>
				
				try {
					val res = interpreter.interpret(command)
					res match {
						case Success( name, value) => {
							ids = ids :+ name
							val res1 = interpreter.interpret(name + ".toHtml")
							res1 match {
								case Success( name1, value1) => EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n" + value1 + "\n")
								case _ => EditorView.data("interpreter") = EditorView.data("interpreter") :+ ("\n" + value + "\n\nscala> ")
							}
						}
						case _ => println("ENTROU NO DEFAULT")
					}
				}
				catch {
					case _ => println("Exception caught. Maybe you have an error on your code?")
				}
			}
			
			//val res = interpreter.interpret(data("code").head)
			
			EditorView.data("code") = data("code")
			//EditorView.data("interpreter") = Seq(EditorView.interpreter_head + results.toString + "\n" + res.toString + "\n\nscala> ")
			EditorView.view(EditorView.data)(NodeSeq.Empty)
	}
}
