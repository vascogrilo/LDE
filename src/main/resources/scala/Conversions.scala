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

/* http://visual-scala.herokuapp.com/repl */  

object Conversions {
	
	var d3BarChartCounter = 0
	var htmlListCounter = 0
	var d3HeapTreeCounter = 0
	
	def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

	
	implicit def fromString(s : java.lang.String) = new Object {
		
		def toHtml = toDisplayableString(s, new java.lang.StringBuilder())
		
		def toDisplayableString( s : String, res: java.lang.StringBuilder ) : String = {
			s match {
				case "" => res toString
				case _ => toDisplayableString(s.tail, res.append(toDisplayableChar(s.head)))
			}
			
		}
		
		def toDisplayableChar( c : Char ) : String = {
			c match {
				case '<' => "&lt;"
				case '>' => "&gt;"
			    case '&' => "&amp;"
			    case '\n' => "<br/>"
			    //case ' ' => "&nbsp;"
				case _ => c toString
			}
		}
	}
	
	
	implicit def fromInt(i : Int) = new Object {
	
		def toHtml = i toString
	}
	
	
	
	implicit def fromXmlElem(e : scala.xml.Elem) = new Object {
		def toHtml = e toString
	}
	
	
	
	implicit def fromXmlNodeBuffer(l : scala.xml.NodeBuffer) = new Object {
		def toHtml = l.map{ e => e }.mkString("","","")
	}
	
	
	
	implicit def fromIterable[A](l : Iterable[A]) = new Object {
		
		def toPlainText: String = l.map{ e => e }.mkString("","","")
		
		def toHtmlList : String = {
			toPlainHtml(l) toString
		}
		
		def toPlainHtml[A](l: Iterable[A]): xml.Elem = {
			<ul>{ l.map(_ match {
				case e:Iterable[_] => <li>{ toPlainHtml(e) }</li>
				case e => <li>{ e }</li>
			})}</ul>
		}
		
		def toHtml: String = {
			htmlListCounter = htmlListCounter + 1
			List("<div class='pagination-box'><div class='paginated-list html_list", htmlListCounter,"'><button id='html_listPrev",htmlListCounter,"' class='btn btn-info list-controls-left'>«</button>",
				"<ul class='html_ul", htmlListCounter, " paginated-list-ul'> </ul>",
				"<button id='html_listNext", htmlListCounter, "' class='btn btn-info list-controls-right'>»</button></div></div>", 
				"<script type='text/javascript'>",  
				"var moreElements", htmlListCounter, " = function() { ", 
					"console.log(\"entrou moreElements com step = \" + window.step", htmlListCounter," + \" e length = \" + window.html_list",htmlListCounter,".length);",
					"if(window.html_list",htmlListCounter,".length == 13) {",
						"console.log(\"if!\");",
						"window.step", htmlListCounter, " += step_incr",htmlListCounter,";",
						"$.ajax({ ",
							"type: 'POST',", 
							//"url: 'http://visual-scala.herokuapp.com/repl',",
							"url: 'http://localhost:8080/repl',",
							"dataType: 'html',", 
							"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",13 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
							"success: function(data) { console.log(data); window.html_list",htmlListCounter," = data.toString().split(\",\"); window.populateList",htmlListCounter,"(); }",
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
							//"url: 'http://visual-scala.herokuapp.com/repl',", 
							"url: 'http://localhost:8080/repl',",
							"dataType: 'html',",
							"data: { code: $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id') + \".slice(\" + window.step", htmlListCounter, " + \",13 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
							"success: function(data) { console.log(data); if(data===\"\") console.log(\"Got empty string from pagination. Something went wrong.\"); window.html_list",htmlListCounter," = data.toString().split(\",\"); window.populateList",htmlListCounter,"(); }",
						"});",
					"}",
				"};",
				"var step", htmlListCounter, " = 0;",
				"var step_incr", htmlListCounter, " = 5;",
				"var html_list",htmlListCounter," = [",toCSVAux(l),"];",
				"var populateList", htmlListCounter, " = function() { ",
					"$('.html_ul", htmlListCounter, "').empty();",
					"console.log(\"vou popular\");",
					"for(var i=0; i<window.html_list",htmlListCounter,".length; i++) { ",
						"console.log(window.html_list",htmlListCounter,"[i]);",
						"$('.html_ul", htmlListCounter, "').append(\"<li>\" + window.html_list",htmlListCounter,"[i].toString() + \"</li>\"); } };",
				"populateList", htmlListCounter, "();", 
				"$('#html_listNext", htmlListCounter, "').click(moreElements", htmlListCounter, ");",
				"$('#html_listPrev", htmlListCounter, "').click(lessElements", htmlListCounter, ");",
			"</script>").mkString("")
		}
		
		def toCSV : String = {
			l.map( _ match {
				case e:Iterable[_] => e.map{ case x => "%s" format(x) } mkString("",",","")
				case e => e toString
			}) mkString("",",","")
		}
		
		def toCSVAux[A](l: Iterable[A]) : String = {
			l.map( _ match {
				case e:Iterable[_] => e.map{ case x => "%s" format(x) } mkString("",",","")
				case e => e toString
			}) mkString("",",","")
		}
    
		def toD3BarChart : String = {
			d3BarChartCounter = d3BarChartCounter + 1
			List("<div style='overflow-x:auto' class='bar-chart", d3BarChartCounter, "'></div>",
				"<script type='text/javascript'>",
				"var dataset",d3BarChartCounter," = [", toCSVAux(l) , "];", 
				"var maxValue",d3BarChartCounter,"= Math.max.apply(Math, dataset",d3BarChartCounter,");",
				"var ratio",d3BarChartCounter," = 280/maxValue",d3BarChartCounter,";",
				"var w = dataset",d3BarChartCounter,".length * 19;", "var h = 300;", "var barPadding = 1;",
				"if(w < 770) w = 770;",
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
				   ".attr('font-size', '11px')",
				   ".attr('font-style','bold')",
				   ".attr('fill', 'black');",
				   "</script>").mkString("")
		}
		
		def toNLSV[A](l: Iterable[A]) : String = {
			l match {
				case Nil => ""
				case x => x.head.toString + "\\n" + toNLSV(x.tail)
			}
		}
		
		def toHeapTree : String = {
			d3HeapTreeCounter = d3HeapTreeCounter + 1
			List("<div id='tree",d3HeapTreeCounter,"'></div>",
				"<style>",
				".link {",
					"fill: none;",
					"stroke: #ccc;",
					"stroke-width: 4.5px;",
				"}</style>",
				"<script type='text/javascript'>",
				"var dados",d3HeapTreeCounter," = \"value\\n",toNLSV(l.drop(1)),"\";",
				"var nodos",d3HeapTreeCounter," = [",l.head,"];",
				"var data",d3HeapTreeCounter," = [{}];",
				"var csv",d3HeapTreeCounter," = d3.csv.parse(dados",d3HeapTreeCounter,");",
				"var i",d3HeapTreeCounter," = 0;",
				"function makeTree",d3HeapTreeCounter,"(nodes) {",
					"data",d3HeapTreeCounter," = [{value: nodos",d3HeapTreeCounter,"[0], children: []}];",
					"nodes.forEach(function(d) {",
					"var nod = {value: d.value};",
					"var parent = data",d3HeapTreeCounter,"[(Math.floor((i",d3HeapTreeCounter,")/2))];",
					"if(parent.children) parent.children.push(nod); else parent.children = [nod];",
					"data",d3HeapTreeCounter,".push(nod);",
					"i",d3HeapTreeCounter,"++;",
					"});",
					"return data",d3HeapTreeCounter,"[0];",
				"}",
				"function drawTree",d3HeapTreeCounter,"() {",
					"var vis = d3.select('#tree",d3HeapTreeCounter,"').append('svg:svg').attr('width', 700).attr('height', 500).append('svg:g').attr('transform', 'translate(20, 20)');",
					"var tree = d3.layout.tree().size([700,400]);",
					"var diagonal = d3.svg.diagonal().projection(function(d) { return [d.x, d.y]; });",
					"var nodes = tree.nodes(makeTree",d3HeapTreeCounter,"(csv",d3HeapTreeCounter,"));",
					"var links = tree.links(nodes);",
					"var link = vis.selectAll('pathlink').data(links).enter().append('svg:path').attr('class', 'link').attr('d', diagonal);",
					"var node = vis.selectAll('g.node').data(nodes).enter().append('svg:g').attr('transform', function(d) { return 'translate(' + d.x + ',' + d.y + ')'; });",
					"node.append('svg:circle').attr('r', 5);",
			        "node.append('svg:text').attr('dx', function(d) { return d.children ? -8 : 8; }).attr('dy', 3).attr('text-anchor', function(d) { return d.children ? 'end' : 'start'; }).text(function(d) { return d.value; })",
				"}",
				"drawTree",d3HeapTreeCounter,"();",
				"</script>").mkString("")
		}
    }
	
	implicit def fromMap2(m: Map[String, Double]) = new Object {
		
		def toHtml: String = <table class='table'> <tr> <th> KEY </th> <th> VALUE </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1.toString } </td> <td> { keyValue._2.toString } </td> </tr> ) } </table> toString
	}
	
	implicit def fromMap[A,B](m : Map[A,B]) = new Object {
	
		def toHtml: String = <table class='table table-hover'> <tr> <th> Key </th> <th> Value </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1.toString } </td> <td> { keyValue._2.toString } </td> </tr> ) } </table> toString 
	}
}

import Conversions._
