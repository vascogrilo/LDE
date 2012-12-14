object IntConversions {
	
	implicit def fromInt(i : Int) = new Object {
		def toHtml = i toString
	}
}

import IntConversions._
