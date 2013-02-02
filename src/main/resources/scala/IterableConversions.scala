object IterableConversions {

	var d3BarChartCounter = 0
	var htmlListCounter = 0
	var d3HeapTreeCounter = 0

	implicit def fromIterable[A](l : Iterable[A]) = new Object {
		
		def toPlainText: String = l.map{ e => e }.mkString("","","")
		
		def toHtmlList : String = {
			htmlListCounter = htmlListCounter + 1
			toPlainHtml(l.toList).toString + "\n<script type='text/javascript'>\n" + createScriptForJoins(l,0) + "dragsort.makeListSortable(document.getElementById(\"list" + htmlListCounter + "\"));\n</script>"
		}
		
		def toPlainHtml[A](l: List[A]): xml.Elem = {
			val name = "edit" + htmlListCounter + "_"
			<ul id={ "list" + htmlListCounter.toString } class={ "sortable boxy" }>{ l.map(_ match {
				case e:Iterable[_] => <li>{ toPlainHtml(e.toList) }</li>
				case e => <li><div style={"cursor: move"} id={ "edit" + htmlListCounter + "_" + l.indexOf(e) + "_View"} class={"view"}>{ e }</div><input id={name + l.indexOf(e) + "_Edit"} class={"inplace"}/></li>
			})}</ul>
		}
		
		def createScriptForJoins[A](l: Iterable[A],index: Int): String = {
			val name = "edit" + htmlListCounter + "_" + index + "_"
			l.size match {
				case 0 => ""
				case _ => ("join(\"" + name + "\",true);\n") + createScriptForJoins(l.tail,index+1)
			}
		}
		
		def toHtml: String = {
			htmlListCounter = htmlListCounter + 1
			List("<div class='pagination-box'>",
					"<div class='paginated-list html_list", htmlListCounter,"'>",
						"<button id='html_listPrev",htmlListCounter,"' class='btn btn-info list-controls-left'>«</button>",
						"<ul id='list",htmlListCounter ,"' class='html_ul", htmlListCounter, " paginated-list-ul'> </ul>",
						"<button id='html_listNext", htmlListCounter, "' class='btn btn-info list-controls-right'>»</button>",
					"</div>",
				"</div>",
				"<button id='updateList",htmlListCounter,"' class='btn btn-link' onclick='modifyList",htmlListCounter,"();'>Modify</button>",
				"<script type='text/javascript'>",
				"var temp_id = $('.html_list", htmlListCounter, "').parent().parent().parent().attr('id');",
				"var result_id = temp_id.substr(0,temp_id.indexOf(\"_TEMPORARYID\"));",
				"function modifyList",htmlListCounter,"() {",
					"var str",htmlListCounter," = \"Iterable(\";",
					"$.each($('.html_ul",htmlListCounter,"').children(), function(index,value) { if(index!=0) str",htmlListCounter,"+= \",\"; str",htmlListCounter,"+= value.innerHTML; });",
					"str",htmlListCounter,"+= \")\";",
					"requestEvaluation(result_id + \".slice(0,\" + step",htmlListCounter," + \") ++ \" + str",htmlListCounter," + \" ++ \" + result_id + \".slice(\" + (step",htmlListCounter,"+13) + \",\" + result_id + \".size)\");",
				"}",
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
							"data: { type: 'partial', code: result_id + \".slice(\" + window.step", htmlListCounter, " + \",13 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
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
							"data: { type: 'partial', code: result_id + \".slice(\" + window.step", htmlListCounter, " + \",13 + \" + window.step", htmlListCounter, " + \") :!: toCSV :!: partial\" },", 
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
						"$('.html_ul", htmlListCounter, "').append(\"<li>\" + window.html_list",htmlListCounter,"[i].toString() + \"</li>\"); } dragsort.makeListSortable(document.getElementById(\"list",htmlListCounter,"\")); };",
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
		
		def toBinaryTree : String = {
			d3HeapTreeCounter = d3HeapTreeCounter + 1
			List("<div id='tree",d3HeapTreeCounter,"'></div>",
				"<style>",
				".link {",
					"fill: none;",
					"stroke: #ccc;",
					"stroke-width: 2px;",
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
					"var w = 26 * ",l.size,";",
					"if(w < 770) w = 770;",
					"var h = 400;", 
					"var vis = d3.select('#tree",d3HeapTreeCounter,"').append('svg:svg').attr('width', w).attr('height', h+50).append('svg:g').attr('transform', 'translate(20, 20)');",
					"var tree = d3.layout.tree().size([w-20,h]);",
					"var diagonal = d3.svg.diagonal().projection(function(d) { return [d.x, d.y]; });",
					"var nodes = tree.nodes(makeTree",d3HeapTreeCounter,"(csv",d3HeapTreeCounter,"));",
					"var links = tree.links(nodes);",
					"var link = vis.selectAll('pathlink').data(links).enter().append('svg:path').attr('class', 'link').attr('d', diagonal);",
					"var node = vis.selectAll('g.node').data(nodes).enter().append('svg:g').attr('transform', function(d) { return 'translate(' + d.x + ',' + d.y + ')'; });",
					"node.append('svg:circle').attr('r', 3);",
			        "node.append('svg:text').attr('dx', function(d) { return d.children ? -8 : 6; }).attr('dy', 4).attr('text-anchor', function(d) { return d.children ? 'end' : 'start'; }).text(function(d) { return d.value; })",
				"}",
				"drawTree",d3HeapTreeCounter,"();",
				"</script>").mkString("")
		}
    }
}

import IterableConversions._
