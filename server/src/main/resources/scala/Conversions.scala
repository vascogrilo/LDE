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

object Conversions {
	
	implicit def fromList[A](l : List[A]) = new Object {
		
		def toHtml = <ul> { l.map(e => <li> { e } </li>) } </ul> toString
    }
    
    implicit def fromSeq[A](s : Seq[A]) = new Object {
		
		def toHtml = <ul> { s.map(e => <li> { e } </li>) } </ul> toString
	}
	
	implicit def fromString(s : String) = new Object {
		
		def toHtml = s
	}
}

import Conversions._
