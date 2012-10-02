package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import com.twitter.util._
import com.twitter.io.TempFile

object ResourceLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case req @ GET(Path(Seg("js" :: file :: Nil))) =>
			logger.debug("GET /js")
			req.respond(ResponseString(readFromFile("/js/" + file)))
		case req @ GET(Path(Seg("css" :: file :: Nil))) =>
			logger.debug("GET /css")
			req.respond(ResponseString(readFromFile("/css/" + file)))
		case req @ GET(Path("/html")) =>
			Redirect("/html/index.html")
		case req @ GET(Path(Seg("html" :: file :: Nil))) =>
			logger.debug("GET /html")
			req.respond(ResponseString(readFromFile("/html/" + file)))
	}
	
	def readFromFile(path: String) : String = {
		io.Source.fromFile(TempFile.fromResourcePath(path)).getLines.mkString("\n")
	}
}
