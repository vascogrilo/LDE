package pt.feup.lde

/**
 * Object Conversions
 * In this object we will store all implicit definitions
 * for converting several data structures into an HTML code
 * for displaying in a web browser
 * 
 */
object Conversions {
	
	implicit def fromList[A](l : List[A]) = new Object {
		
		def toHtml = <ul> { l.map(e => <li> { e } </li>) } </ul> toString
    }
}
