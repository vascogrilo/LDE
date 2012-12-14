object XMLConversions {

	implicit def fromXmlElem(e : scala.xml.Elem) = new Object {
		def toHtml = e toString
	}
	
	
	implicit def fromXmlNodeBuffer(l : scala.xml.NodeBuffer) = new Object {
		def toHtml = l.map{ e => e }.mkString("","","")
	}
}

import XMLConversions._
