package com.example

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

import java.io.{File, FileOutputStream, FileWriter}
import scala.io.Source

import com.twitter.io.TempFile
import com.twitter.util._

/** Asynchronous plan that gets the time in a ridiculous fashion.
 *  (But imagine that it's using a vital external HTTP service to
 *  inform its response--this is a fine way to do that.) */
object Time extends async.Plan
  with ServerErrorResponse {
  val logger = org.clapper.avsl.Logger(getClass)
  
  def intent = {
    case req @ GET(Path("/time")) => 
      logger.debug("GET /time")
      import dispatch._
      // the call below is non-blocking, so we return quickly
      // and free netty's worker thread
      //Server.http(:/("127.0.0.1", 8080).POST / "time" >- { time =>
        //val outcome = (new Eval).apply[ () => String ](TempFile.fromResourcePath("/InsertionSort.scala"))
		//req.respond(ResponseString(outcome.toString))
		val sourceFile = TempFile.fromResourcePath("/InsertionSort.scala")
		req.respond(ResponseString(sourceFile.toString))
    case req @ POST(Path("/time")) =>
      logger.debug("POST /time")
      // since we don't have to do any blocking IO for this request
      // we can call respond right way
      req.respond(ResponseString("{\"Name\":\"John Smith\",\"Age\":32,\"Employed\":true,\"Address\":{\"Street\":\"701 First Ave.\",\"City\":\"Sunnyvale, CA 95125\",\"Country\":\"United States\"},\"Children\":[{\"Name\":\"Richard\",\"Age\":7},{\"Name\":\"Susan\",\"Age\":4},{\"Name\":\"James\",\"Age\":3}]}"))
  }
}
