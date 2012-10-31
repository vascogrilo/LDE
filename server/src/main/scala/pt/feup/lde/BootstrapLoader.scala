package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

/**
 * RESOURCE LOADER
 * 
 * Object to load (read and return) contents of the Twitter's Bootstrap library
 * The library is under the resources folder.
 * 
 * There are 3 folders that can be loaded
 * bootstrap/css
 * bootstrap/img
 * bootstrap/js
 * 
 */
object ExtJsLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
	
		case req @ GET(Path(Seg("bootstrap" :: "css" :: file :: Nil))) =>
			req.respond(ResponseString(Misc.readFromFile(getClass,"/bootstrap/css/" + file)))
		
		case req @ GET(Path(Seg("bootstrap" :: "img" :: file :: Nil))) =>
			req.respond(ResponseString(Misc.readFromFile(getClass,"/bootstrap/img/" + file)))
			
		case req @ GET(Path(Seg("bootstrap" :: "js" :: file :: Nil))) =>
			req.respond(ResponseString(Misc.readFromFile(getClass,"/bootstrap/js/" + file)))
	}
}
