package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

import java.io.{File, FileNotFoundException, BufferedOutputStream, FileOutputStream}


object ResourceLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case req @ GET(Path(Seg("js" :: file :: Nil))) =>
			logger.debug("GET /js")
			req.respond(ResponseString(readFromFile(getClass,"/js/" + file)))
		case req @ GET(Path(Seg("css" :: file :: Nil))) =>
			logger.debug("GET /css")
			req.respond(ResponseString(readFromFile(getClass,"/css/" + file)))
		case req @ GET(Path("/html")) =>
			Redirect("/html/index.html")
		case req @ GET(Path(Seg("html" :: file :: Nil))) =>
			logger.debug("GET /html")
			req.respond(ResponseString(readFromFile(getClass,"/html/" + file)))
	}
	
	def readFromFile(klass: Class[_], path: String) : String = {
		klass.getResourceAsStream(path) match {
			case null =>
				throw new FileNotFoundException(path)
			case stream =>
		        io.Source.fromInputStream(stream).getLines().mkString("\n")
		}
	}
}
