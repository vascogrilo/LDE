object Conversions {
	
	implicit def fromList[A](l : List[A]) = new Object {
		
		def toHtml = <ul> { l.map(e => <li> { e } </li>) } </ul> toString
    }
}

import Conversions._
