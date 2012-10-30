package pt.feup.lde

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._

object ExtJsLoader extends async.Plan with ServerErrorResponse {
	
	val logger = org.clapper.avsl.Logger(getClass)
	
	def intent = {
		
		////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * 
		 * RESOURCE LOADING FOR ALL FILES OF THE EXT JS LIBRARY
		 * 
		 * 
		 */
		///////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/FILE
		 */
		case req @ GET(Path(Seg("output" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/app/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "app" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/app/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/data/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "data" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/data/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/data/association/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "data" :: "association" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/data/association/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/data/proxy/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "data" :: "proxy" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/data/proxy/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/data/reader/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "data" :: "reader" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/data/reader/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/data/writer/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "data" :: "writer" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/data/writer/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/tip/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "tip" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/tip/" + file)))
			
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/util/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "util" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/util/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/state/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "state" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/state/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/panel/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "panel" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/panel/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/dd/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "dd" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/dd/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/container/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "container" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/container/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/toolbar/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "toolbar" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/toolbar/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/draw/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "draw" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/draw/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/draw/engine/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "draw" :: "engine" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/draw/engine/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/button/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "button" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/button/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/menu/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "menu" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/menu/" + file)))
			
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/fx/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "fx" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/fx/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/fx/target/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "fx" :: "target" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/fx/target/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/layout/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "layout" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/layout/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/layout/component/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "layout" :: "component" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/layout/component/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/layout/container/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "layout" :: "container" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/layout/container/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/layout/container/boxOverflow/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "layout" :: "container" :: "boxOverflow" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/layout/container/boxOverflow/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/window/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "window" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/window/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/src/resizer/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "src" :: "resizer" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/src/resizer/" + file)))
		
			
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/resources/FILE
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "resources" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/resources/" + file)))
		
		
		/**
		 * Ext JS Library sources.
		 * PATH: /output/extjs/resources/css/FILE.css
		 */
		case req @ GET(Path(Seg("output" :: "extjs" :: "resources" :: "css" :: file :: Nil))) =>
			logger.debug("GET /output")
			req.respond(ResponseString(Misc.readFromFile(getClass,"/output/extjs/resources/css/" + file)))	
	}
}
