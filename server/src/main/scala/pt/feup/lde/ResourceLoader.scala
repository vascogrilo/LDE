package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

object ResourceLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		
		/**
		 * JavaScript sources.
		 * PATH : /js/FILE.js
		 */
		case req @ GET(Path(Seg("js" :: file :: Nil))) =>
			logger.debug("GET /js")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/js/" + file)))
		
		
		/**
		 * CSS sources.
		 * PATH : /css/FILE.css
		 */
		case req @ GET(Path(Seg("css" :: file :: Nil))) =>
			logger.debug("GET /css")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/css/" + file)))
		
		
		/**
		 * HTML sources.
		 * PATH : /html/FILE.html
		 */
		case req @ GET(Path("/html")) =>
			Redirect("/html/index.html")
		
		case req @ GET(Path(Seg("html" :: file :: Nil))) =>
			logger.debug("GET /html")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/html/" + file)))
	}
}
