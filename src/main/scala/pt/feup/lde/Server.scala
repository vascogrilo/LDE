package pt.feup.lde

import unfiltered.netty.Http

/** embedded server */
object Server {
  val logger = org.clapper.avsl.Logger(Server.getClass)
  val http = new dispatch.nio.Http

  def main(args: Array[String]) {
    Http(8080)
		.handler(ScalaEditor)
		.handler(ResourceLoader)
		.handler(BootstrapLoader)
		.handler(D3Loader)
		.run { s =>
			logger.info("starting unfiltered app at localhost on port %s".format(s.port))
		}
    http.shutdown()
  }
}
