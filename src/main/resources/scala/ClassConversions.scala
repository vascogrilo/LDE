object ClassConversions {

	implicit def fromClasses(c: AnyRef) = new Object {
		
		def toHtml = c toString
		
		val cl = c.getClass
		val name = cl.getName
		val annotations = cl.getAnnotations
		val constructors = cl.getDeclaredConstructors
		
		def toClass = {
			"<table class='table'><tr><th>Element</th><th>Value</th></tr>" +
			"<tr><td>Name</td><td>" + name.slice(name.lastIndexOf("$")+1,name.length) + "</td></tr>" +
			( if(annotations.size > 0)
				"<tr><td>Annotations</td>" + ( annotations.map(x => "<td>" + x + "</td>") mkString(",") ) + "</tr>"
			  else ""
			) +
			( if(constructors.size > 0)
				"<tr><td>Constructors</td>" + ( constructors.map(x => "<td>" + x + "</td>") mkString(",") ) + "</tr>"
			  else ""
			) +
			"</table>"
		}
	}
}

import ClassConversions._
