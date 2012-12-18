object ClassConversions {

	implicit def fromClasses(c: AnyRef) = new Object {
		
		val name = c.getClass.getName
		
		def toHtml = <h2>{ name }</h2> toString 
	}
}

import ClassConversions._
