package com.example

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

object Interpret extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		case GET(Path("/scala")) => 
			logger.debug("GET /scala")
			view(Map.empty)
	}
	
	def view(params: Map[String, Seq[String]]) = {
    Html(
		<html>
		<head>
		<script src="../resources/scripts/bcl.js"></script>
		</head>
		<body>
		<br/>
		<br/>
		<br/>
		<center>
		<form id="bclform" onSubmit="return bcl_go( this );">
		<nobr>
		<b style="font-size: 120%; font-family: monospace; font-weight: bold">%</b>
		<input style="background-color: #e0e0e0; font-family: monospace; font-size: 120%; font-weight: bold"
		id="bclline" type="text" name="cmd" size="80"/>
		</nobr>
		</form>
		</center>
		</body>
		</html>
	)
  }
}
