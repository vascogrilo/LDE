package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

/**
 * RESOURCE LOADER
 * 
 * Object to load (read and return) contents of the D3 Data-Driven Documents Library
 * The library is under the resources folder.
 * 
 */
object D3Loader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
	
		case req @ GET(Path(Seg("d3" :: file :: Nil))) =>
			req.respond(ResponseString(Misc.readFromFile(getClass,"/d3/" + file)))
	}
}
