object MapConversions {
	
	var pieChartCounter = 0
	
	implicit def fromMap[A,B](m : Map[A,B]) = new Object {
		def toHtml: String = <table class='table table-hover'> <tr> <th> Key </th> <th> Value </th> </tr> { m.map( keyValue => <tr> <td> { keyValue._1.toString } </td> <td> { keyValue._2.toString } </td> </tr> ) } </table> toString 
	
		def toPieChart: String = {
			pieChartCounter = pieChartCounter + 1
			List("<div id='pieChart",pieChartCounter,"' class='pieChart'></div>",
				"<style>",
				".pieChart {",
					"font: 10px sans-serif;",
				"}",
				".arc path {",
					"stroke: #fff;",
				"}​</style>",
				"<script type='text/javascript'>",
					"var dados",pieChartCounter," = \"key,value\\n" + { m.map( x => x._1.toString + "," + x._2.toString + "\\n") mkString("") } + "\";",
					"var data",pieChartCounter," = d3.csv.parse(dados",pieChartCounter,");",
					"var width = 770, height = 400, radius = Math.min(width, height) / 2;",
					"var color = d3.scale.ordinal().range(['#98abc5', '#8a89a6', '#7b6888', '#6b486b', '#a05d56', '#d0743c', '#ff8c00']);",
					"var arc = d3.svg.arc().outerRadius(radius - 10).innerRadius(0);",
					"var pie",pieChartCounter," = d3.layout.pie().sort(null).value(function(d) { return d.value; });",
					"var svg = d3.select('#pieChart",pieChartCounter,"').append('svg').attr('width', width).attr('height', height).append('g').attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')');",
					"data",pieChartCounter,".forEach(function(d) { d.population = +d.population; });",
					"var g = svg.selectAll('.arc').data(pie",pieChartCounter,"(data",pieChartCounter,")).enter().append('g').attr('class', 'arc');",				
					"g.append('path').attr('d', arc).style('fill', function(d) { return color(d.data.key); });",
					"g.append('text').attr('transform', function(d) { return 'translate(' + arc.centroid(d) + ')'; }).attr('dy', '.35em').style('text-anchor', 'middle').text(function(d) { return d.data.key; });",
				"</script>").mkString("")
		}
	}
}

import MapConversions._
