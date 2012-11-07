/**
 * Object Conversions
 * In this object we will store all implicit definitions
 * for converting several data structures into an HTML code
 * for displaying in a web browser
 * 
 * This is intended (for now) as a resource file in which we will store
 * conversions and then import every conversion in the object
 * so we can inject this at runtime into the Scala's Interpreter
 */

object Conversions {
	
	var d3BarChartCounter = 0
	
	implicit def fromString(s : String) = new Object {
		
		def toHtml = s
	}
	
	implicit def fromInt(i : Int) = new Object {
	
		def toHtml = i toString
	}
	
	implicit def fromList[A](l : List[A]) = new Object {
		
		def toHtml = <div class='pagination'> <ul> <li><a href='#'>Prev</a></li> { l.map(e => <li><a href='#'>{ e }</a></li>) } <li><a href='#'>Next</a></li> </ul> </div> toString
    
		def toD3BarChart = {
			d3BarChartCounter = d3BarChartCounter + 1
			"<div class='bar-chart" + d3BarChartCounter + "'></div>" +
			"<script type='text/javascript'>" +
				"var w = 700;" +
				"var h = 200;" + 
				"var barPadding = 1;" +
				"var dataset = [ " + { l map{ case e => "%d" format (e) }  mkString("",",",",") } + " 0 ];" +
				"var svg = d3.select('.bar-chart" + d3BarChartCounter + "')" +
							".append('svg')" +
							".attr('width', w)" +
							".attr('height', h);" +
				"svg.selectAll('rect')" +
				   ".data(dataset)" + 
				   ".enter()" + 
				   ".append('rect')" + 
				   ".attr('x', function(d, i) {" + 
				   		" return i * (w / dataset.length);" + 
				   "})" + 
				   ".attr('y', function(d) {" + 
				   		" return h - d;" +
				   "})" + 
				   ".attr('width', w / dataset.length - barPadding)" + 
				   ".attr('height', function(d) { " +
				   		"return d * 4; })" +
				   ".attr('fill', function(d) { return 'rgb(0,' + (128 - d) + ',' + (128 - d) + ')'; });" + 
				"svg.selectAll('text')" + 
				   ".data(dataset)" + 
				   ".enter()" + 
				   ".append('text')" + 
				   ".text(function(d) { return d; })" + 
				   ".attr('x', function(d, i) { return i * (w / dataset.length) + 5; })" +
				   ".attr('y', function(d) { return h - d + 15; })" + 
				   ".attr('font-family', 'sans-serif')" + 
				   ".attr('font-size', '11px')" + 
				   ".attr('fill', 'white');" +
				   "</script>"
		}
    }
    
    implicit def fromRange(r : Range) = new Object {
		
		def toD3BarChart = {
			d3BarChartCounter = d3BarChartCounter + 1
			"<div class='bar-chart" + d3BarChartCounter + "'></div>" +
			"<script type='text/javascript'>" +
				"var w = 700;" +
				"var h = 200;" + 
				"var barPadding = 1;" +
				"var dataset = [ " + { r map{ case e => "%d" format (e) }  mkString("",",",",") } + " 0 ];" +
				"var svg = d3.select('.bar-chart" + d3BarChartCounter + "')" +
							".append('svg')" +
							".attr('width', w)" +
							".attr('height', h);" +
				"svg.selectAll('rect')" +
				   ".data(dataset)" + 
				   ".enter()" + 
				   ".append('rect')" + 
				   ".attr('x', function(d, i) {" + 
				   		" return i * (w / dataset.length);" + 
				   "})" + 
				   ".attr('y', function(d) {" + 
				   		" return h - d;" +
				   "})" + 
				   ".attr('width', w / dataset.length - barPadding)" + 
				   ".attr('height', function(d) { " +
				   		"return d * 4; })" +
				   ".attr('fill', function(d) { return 'rgb(0,' + (128 - d) + ',' + (128 - d) + ')'; });" + 
				"svg.selectAll('text')" + 
				   ".data(dataset)" + 
				   ".enter()" + 
				   ".append('text')" + 
				   ".text(function(d) { return d; })" + 
				   ".attr('x', function(d, i) { return i * (w / dataset.length) + 5; })" +
				   ".attr('y', function(d) { return h - d + 15; })" + 
				   ".attr('font-family', 'sans-serif')" + 
				   ".attr('font-size', '11px')" + 
				   ".attr('fill', 'white');" +
				   "</script>"
		}
    }
	
	implicit def fromMap[A,B](m : Map[A,B]) = new Object {
	
		def toHtml = <table class='table table-stripped'> <tr> <th> Key </th> <th> Value </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1 } </td> <td> { keyValue._2 } </td> </tr> ) } </table> toString 
	}
}

import Conversions._
