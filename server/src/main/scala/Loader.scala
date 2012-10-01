package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import com.twitter.util._
import com.twitter.io.TempFile

object Loader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case req @ GET(Path(Seg("js" :: file :: Nil))) =>
			logger.debug("GET /js")
			req.respond(ResponseString(readFromFile("/js/" + file)))
		case req @ GET(Path(Seg("css" :: file :: Nil))) =>
			logger.debug("GET /css")
			req.respond(ResponseString(readFromFile("/css/" + file)))
	}
	
	def readFromFile(path: String) : String = {
		val lines = io.Source.fromFile(TempFile.fromResourcePath(path)).getLines
		var source: String = ""
		while(lines.hasNext){
			source = source + lines.next
		}
		return source
	}
}
