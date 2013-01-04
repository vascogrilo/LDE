/**
 * Object Conversions
 * In this object we will store all implicit definitions
 * for converting several data structures into an HTML code
 * for displaying in a web browser
 * 
 * This is intended (for now) as a resource file in which we will store
 * conversions and then import every conversion in the object
 * so we can inject this at runtime into the Scala's Interpreter
 */

/* http://visual-scala.herokuapp.com/repl */  

object Conversions {
	
	def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]
}

import Conversions._
