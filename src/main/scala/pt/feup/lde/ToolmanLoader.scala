package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._
import pt.feup.lde.Utilities.Misc._

/**
 * RESOURCE LOADER
 * 
 * Object to load (read and return) contents of the ToolMan Js library.
 * The library is under the resources folder.
 * 
 */
object ToolmanLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
	
		case req @ GET(Path(Seg("toolbear" :: file :: Nil))) =>
			req.respond(ResponseString(readFromFile(getClass,"/toolbear/" + file)))
	}
}
