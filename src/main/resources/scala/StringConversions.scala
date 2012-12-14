object StringConversions {

	implicit def fromString(s : java.lang.String) = new Object {
		
		def toHtml = toDisplayableString(s, new java.lang.StringBuilder())
		
		def toDisplayableString( s : String, res: java.lang.StringBuilder ) : String = {
			s match {
				case "" => res toString
				case _ => toDisplayableString(s.tail, res.append(toDisplayableChar(s.head)))
			}
		}
		
		def toDisplayableChar( c : Char ) : String = {
			c match {
				case '<' => "&lt;"
				case '>' => "&gt;"
			    case '&' => "&amp;"
			    case '\n' => "<br/>"
				case _ => c toString
			}
		}
	}
}

import StringConversions._
