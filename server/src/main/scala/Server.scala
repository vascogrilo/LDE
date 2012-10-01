package com.example

import java.net.URL

/** embedded server */
object Server {
  val logger = org.clapper.avsl.Logger(Server.getClass)
  val http = new dispatch.nio.Http

  def main(args: Array[String]) {
    unfiltered.netty.Http(8080)
		.handler(Palindrome)
		.handler(Interpret)
		.handler(Time)
		.handler(Loader)
		.run { s =>
			logger.info("starting unfiltered app at localhost on port %s".format(s.port))
		}
    http.shutdown()
  }
}
