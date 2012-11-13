package pt.feup.lde

import unfiltered.netty.Http

/** embedded server */
object Server {
  val logger = org.clapper.avsl.Logger(Server.getClass)
  val http = new dispatch.nio.Http
  val port = Option(System.getenv("PORT")) map { s => Integer.parseInt(s) }
  
  def main(args: Array[String]) {
    Http(port.getOrElse(8080))
		.handler(ScalaEditor)
		.handler(ResourceLoader)
		.handler(BootstrapLoader)
		.handler(D3Loader)
		.run { s =>
			println("Started Unfiltered Server on port %s".format(s.port))
		}
    http.shutdown()
  }
}
