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

/* http://evening-beach-6577.herokuapp.com/repl */  

object Conversions {
	
	var d3BarChartCounter = 0
	var htmlListCounter = 0
	var d3HeapTreeCounter = 0
	
	implicit def fromString(s : String) = new Object {
		
		def toHtml = s
	}
	
	implicit def fromInt(i : Int) = new Object {
	
		def toHtml = i toString
	}
	
	implicit def fromIterable[A](l : Iterable[A]) = new Object {
		
		def toHtml = {
			htmlListCounter = htmlListCounter + 1
			List("<div class='pagination-box'><div class='paginated-list html_list", htmlListCounter,"'><button id='html_listPrev",htmlListCounter,"' class='btn btn-info list-controls-left'>«</button>",
				"<ul class='html_ul", htmlListCounter, " paginated-list-ul'> </ul>",
				"<button id='html_listNext", htmlListCounter, "' class='btn btn-info list-controls-right'>»</button></div></div>", 
				"<script type='text/javascript'>", 
					"var step", htmlListCounter, " = 0;",
					"var step_incr", htmlListCounter, " = 5;",
					"var html_list", htmlListCounter, " = [", { l.map{ case e => "%s" format(e) } mkString("",",","") }, "];",
					"var populateList", htmlListCounter, " = function() { ",
						"$('.html_ul", htmlListCounter, "').empty();",
						"console.log(\"vou popular\");",
						"for(var i=0; i<window.html_list",htmlListCounter,".length; i++) { ",
							"console.log(window.html_list",htmlListCounter,"[i]);",
							"$('.html_ul", htmlListCounter, "').append(\"<li>\" + window.html_list",htmlListCounter,"[i] + \"</li>\"); } };", 
					"var moreElements", htmlListCounter, " = function() { ", 
						"console.log(\"entrou moreElements com step = \" + window.step", htmlListCounter," + \" e length = \" + window.html_list",htmlListCounter,".length);",
						"if(window.html_list",htmlListCounter,".length == 20) {",
							"console.log(\"if!\");",
							"window.step", htmlListCounter, " += step_incr",htmlListCounter,";",
							"$.ajax({ ",
								"type: 'POST',", 
								"url: 'http://evening-beach-6577.herokuapp.com/repl',", 
								"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",10 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
								"success: function(data) { console.log(data); window.html_list",htmlListCounter," = data.split(\",\"); window.populateList",htmlListCounter,"(); }",
							"});",
					"}",
				"};",
				"var lessElements", htmlListCounter, " = function() { ",
					"console.log(\"entrou lessElements com step = \" + window.step", htmlListCounter, ");",
					"if(window.step", htmlListCounter, " - step_incr",htmlListCounter," >= 0) { ",
						"console.log(\"if!\");",
						"window.step", htmlListCounter, " -= step_incr",htmlListCounter,";",
						"$.ajax({ ",
							"type: 'POST',", 
							"url: 'http://evening-beach-6577.herokuapp.com/repl',", 
							"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",10 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
							"success: function(data) { console.log(data); window.html_list",htmlListCounter," = data.split(\",\"); window.populateList",htmlListCounter,"(); }",
						"});",
					"}",
				"};",
				"populateList", htmlListCounter, "();", 
				"$('#html_listNext", htmlListCounter, "').click(moreElements", htmlListCounter, ");",
				"$('#html_listPrev", htmlListCounter, "').click(lessElements", htmlListCounter, ");",
			"</script>").mkString("")
		}
		
		def toCSV = {
			l.map{ case e => "%d" format(e) } mkString("",",","")
		}
    
		def toD3BarChart = {
			d3BarChartCounter = d3BarChartCounter + 1
			List("<div class='bar-chart", d3BarChartCounter, "'></div>",
				"<script type='text/javascript'>", "var w = 700;", "var h = 300;", "var barPadding = 1;",
				"var dataset",d3BarChartCounter," = [ ", { l map{ case e => "%d" format (e) }  mkString("",",","") }, "];", 
				"var maxValue",d3BarChartCounter,"= Math.max.apply(Math, dataset",d3BarChartCounter,");",
				"var ratio",d3BarChartCounter," = 280/maxValue",d3BarChartCounter,";",
				"var svg = d3.select('.bar-chart", d3BarChartCounter, "')",
							".append('svg')",
							".attr('width', w)",
							".attr('height', h);",
				"svg.selectAll('rect')",
				   ".data(dataset",d3BarChartCounter,")",
				   ".enter()",
				   ".append('rect')", 
				   ".attr('x', function(d, i) {", 
				   		" return i * (w / dataset",d3BarChartCounter,".length);", 
				   "})",
				   ".attr('y', function(d) {", 
				   		" return h - (d * ratio",d3BarChartCounter,");",
				   "})",
				   ".attr('width', w / dataset",d3BarChartCounter,".length - barPadding)",
				   ".attr('height', function(d) { ",
				   		"return d * ratio",d3BarChartCounter,"; })",
				   ".attr('fill', function(d) { return 'rgb(0,' + (128 - (d)) + ',' + (128 - (d)) + ')'; });", 
				"svg.selectAll('text')",
				   ".data(dataset",d3BarChartCounter,")",
				   ".enter()",
				   ".append('text')",
				   ".text(function(d) { return d; })",
				   ".attr('x', function(d, i) { return i * (w / dataset",d3BarChartCounter,".length) + 5; })",
				   ".attr('y', function(d) { return h - ( (d * ratio",d3BarChartCounter,") + 5); })",
				   ".attr('font-family', 'sans-serif')",
				   ".attr('font-size', '12px')",
				   ".attr('fill', 'black');",
				   "</script>").mkString("")
		}
		
		def toHeapTree = {
			d3HeapTreeCounter = d3HeapTreeCounter + 1
			List("<div class='d3tree",d3HeapTreeCounter,"'></div>",
				"<style type='text/css'>",
				".node {  fill: #fff; stroke: #000;  stroke-width: 1px; } .link { fill: none;  stroke: #000; }</style>",
				"<script type='text/javascript'>",
				"var index",d3HeapTreeCounter," = 1;",
				"var vals",d3HeapTreeCounter," = [", l map {case e => "%s" format(e) } mkString("",",",""),"];",
				"var w = 750,h = 500,root = {},data = [root],tree = d3.layout.tree().size([w - 20, h - 20]),diagonal = d3.svg.diagonal(),duration = 500,timer",d3HeapTreeCounter," = setInterval(update",d3HeapTreeCounter,", duration);",
				"var vis = d3.select('.d3tree",d3HeapTreeCounter,"').append('svg:svg').attr('width', w).attr('height', h).append('svg:g').attr('transform', 'translate(10, 10)');",
				"vis.selectAll('circle').data(tree(root)).enter().append('svg:circle').attr('class', 'node').attr('r', 10).attr('cx', x).attr('cy', y).append('svg:title').text(vals",d3HeapTreeCounter,"[0]);",
				"function update",d3HeapTreeCounter,"() {",
				  "if (index",d3HeapTreeCounter," == vals",d3HeapTreeCounter,".length) return clearInterval(timer",d3HeapTreeCounter,");",
				  "var d = {id: index",d3HeapTreeCounter,", value: vals",d3HeapTreeCounter,"[index",d3HeapTreeCounter,"]}, parent = data[Math.floor((index",d3HeapTreeCounter,"-1)/2)];",
				  "if (parent.children) parent.children.push(d); else parent.children = [d];",
				  "data.push(d);",
				  "index",d3HeapTreeCounter," = index",d3HeapTreeCounter," + 1;",
				  "var nodes = tree(root);",
				  "var node = vis.selectAll('circle.node').data(nodes, nodeId);",
				  "node.enter().append('svg:circle').attr('class', 'node').attr('r', 10).attr('cx', function(d) { return d.parent.data.x0; }).attr('cy', function(d) { return d.parent.data.y0; }).append('svg:title').text(d.value).transition().duration(duration).attr('cx', x).attr('cy', y);",
				  "node.transition().duration(duration).attr('cx', x).attr('cy', y);",
				  "var link = vis.selectAll('path.link').data(tree.links(nodes), linkId);",
				  "link.enter().insert('svg:path', 'circle').attr('class', 'link').attr('d', function(d) { var o = {x: d.source.data.x0, y: d.source.data.y0}; return diagonal({source: o, target: o}); }).transition().duration(duration).attr('d', diagonal);",
				  "link.transition().duration(duration).attr('d', diagonal);",
				"}",
				"function linkId(d) {",
				  "return d.source.data.id + '-' + d.target.data.id;",
				"}",
				"function nodeId(d) {",
				  "return d.data.id;",
				"}",
				"function x(d) {",
				  "return d.data.x0 = d.x;",
				"}",
				"function y(d) {",
				  "return d.data.y0 = d.y;",
				"}</script>"
			).mkString("")
		}
    }
    
    implicit def fromRange(r : Range) = new Object {
		
		def toD3BarChart = {
			d3BarChartCounter = d3BarChartCounter + 1
			List("<div class='bar-chart",d3BarChartCounter,"'></div>",
			"<script type='text/javascript'>",
				"var w = 700;",
				"var h = 300;", 
				"var barPadding = 1;",
				"var dataset",d3BarChartCounter," = [ ",{ r map{ case e => "%d" format (e) }  mkString("",",","") },"];",
				"var maxValue",d3BarChartCounter,"= Math.max.apply(Math, dataset",d3BarChartCounter,");",
				"var ratio",d3BarChartCounter," = (h - 20)/maxValue",d3BarChartCounter,";",
				"var svg = d3.select('.bar-chart",d3BarChartCounter,"')",
							".append('svg')",
							".attr('width', w)",
							".attr('height', h);",
				"svg.selectAll('rect')",
				   ".data(dataset",d3BarChartCounter,")",
				   ".enter()",
				   ".append('rect')", 
				   ".attr('x', function(d, i) {", 
				   		" return i * (w / dataset",d3BarChartCounter,".length);", 
				   "})",
				   ".attr('y', function(d) {", 
				   		" return h - (d * ratio",d3BarChartCounter,");",
				   "})",
				   ".attr('width', w / dataset",d3BarChartCounter,".length - barPadding)", 
				   ".attr('height', function(d) { ",
				   		"return d * ratio",d3BarChartCounter,"; })",
				   ".attr('fill', function(d) { return 'rgb(0,' + (128 - d) + ',' + (128 - d) + ')'; });", 
				"svg.selectAll('text')",
				   ".data(dataset",d3BarChartCounter,")",
				   ".enter()",
				   ".append('text')", 
				   ".text(function(d) { return d; })", 
				   ".attr('x', function(d, i) { return i * (w / dataset",d3BarChartCounter,".length) + 5; })",
				   ".attr('y', function(d) { return ( h - (d * ratio",d3BarChartCounter," + 5) ); })",
				   ".attr('font-family', 'sans-serif')",
				   ".attr('font-size', '12px')",
				   ".attr('fill', 'black');",
				   "</script>").mkString("")
		}
    }
	
	implicit def fromMap[A,B](m : Map[A,B]) = new Object {
	
		def toHtml = <table class='table table-hover table-condensed'> <tr> <th> Key </th> <th> Value </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1 } </td> <td> { keyValue._2 } </td> </tr> ) } </table> toString 
	}
}

import Conversions._
