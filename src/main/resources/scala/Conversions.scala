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
	var htmlListCounter = 0
	
	implicit def fromString(s : String) = new Object {
		
		def toHtml = s
	}
	
	implicit def fromInt(i : Int) = new Object {
	
		def toHtml = i toString
	}
	
	implicit def fromList[A](l : List[A]) = new Object {
		
		def toHtml = {
			htmlListCounter = htmlListCounter + 1
			List("<div class='pagination-box'><div class='paginated-list html_list", htmlListCounter,"'><ul class='ul_control'> <li id='html_listPrev",htmlListCounter,"' class='list-controls'>«</li> </ul>",
				"<ul class='html_ul", htmlListCounter, "'> </ul>",
				"<ul class='ul_control'> <li id='html_listNext", htmlListCounter, "' class='list-controls'>»</li> </ul></div></div>", 
				"<script type='text/javascript'>", 
					"var step", htmlListCounter, " = 0;", 
					"var html_list", htmlListCounter, " = [", { l.map{ case e => "%d" format(e) } mkString("",",","") }, "];",
					"var populateList", htmlListCounter, " = function(values,length) { ",
						"$('.html_ul", htmlListCounter, "').empty();",
						"for(var i=0; i<length; i++) { ",  
						"$('.html_ul", htmlListCounter, "').append(\"<li>\" + values[i] + \"</li>\"); } };", 
					"var moreElements", htmlListCounter, " = function() { ", 
					"if(window.step", htmlListCounter, " < html_list", htmlListCounter, ".length) {",
						"window.step", htmlListCounter, " += 3;",
						"$.ajax({ ",
							"type: 'POST',", 
							"url: 'http://localhost:8080/repl',", 
							"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",10 + \" + window.step", htmlListCounter, " + \") :!: toPartialHtml :!: partial\" },", 
							"success: function(data) { console.log(data); $('.html_ul", htmlListCounter, "').empty(); $('.html_ul", htmlListCounter, "').append(data); }",
						"});", 
					"}",
				"};",
				"var lessElements", htmlListCounter, " = function() { ",
					"if(window.step", htmlListCounter, " > 0) { ",
						"window.step", htmlListCounter, " -= 3;", 
						"$.ajax({ ",
							"type: 'POST',", 
							"url: 'http://localhost:8080/repl',", 
							"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",10 + \" + window.step", htmlListCounter, " + \") :!: toPartialHtml :!: partial\" },", 
							"success: function(data) { console.log(data); $('.html_ul", htmlListCounter, "').empty(); $('.html_ul", htmlListCounter, "').append(data); }",
						"});",
					"} else window.step", htmlListCounter, "=0;",
				"};",
				"populateList", htmlListCounter, "(html_list", htmlListCounter, ",html_list", htmlListCounter, ".length);", 
				"$('#html_listNext", htmlListCounter, "').click(moreElements", htmlListCounter, ");",
				"$('#html_listPrev", htmlListCounter, "').click(lessElements", htmlListCounter, ");",
			"</script>").mkString("")
		}
		
		def toPartialHtml = {
			l.map{ case e => "<li>%d</li>" format(e) } mkString("","","")
		}
    
		def toD3BarChart = {
			d3BarChartCounter = d3BarChartCounter + 1
			List("<div class='bar-chart", d3BarChartCounter, "'></div>",
				"<script type='text/javascript'>", "var w = 700;", "var h = 200;", "var barPadding = 1;",
				"var dataset = [ ", { l map{ case e => "%d" format (e) }  mkString("",",","") }, "];", 
				"var svg = d3.select('.bar-chart", d3BarChartCounter, "')",
							".append('svg')",
							".attr('width', w)",
							".attr('height', h);",
				"svg.selectAll('rect')",
				   ".data(dataset)",
				   ".enter()",
				   ".append('rect')", 
				   ".attr('x', function(d, i) {", 
				   		" return i * (w / dataset.length);", 
				   "})",
				   ".attr('y', function(d) {", 
				   		" return h - d;",
				   "})",
				   ".attr('width', w / dataset.length - barPadding)",
				   ".attr('height', function(d) { ",
				   		"return d * 4; })",
				   ".attr('fill', function(d) { return 'rgb(0,' + (128 - d) + ',' + (128 - d) + ')'; });", 
				"svg.selectAll('text')",
				   ".data(dataset)",
				   ".enter()",
				   ".append('text')",
				   ".text(function(d) { return d; })",
				   ".attr('x', function(d, i) { return i * (w / dataset.length) + 5; })",
				   ".attr('y', function(d) { return h - d + 15; })",
				   ".attr('font-family', 'sans-serif')",
				   ".attr('font-size', '11px')",
				   ".attr('fill', 'white');",
				   "</script>").mkString("")
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
	
		def toHtml = <table class='table table-hover table-condensed'> <tr> <th> Key </th> <th> Value </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1 } </td> <td> { keyValue._2 } </td> </tr> ) } </table> toString 
	}
}

import Conversions._
