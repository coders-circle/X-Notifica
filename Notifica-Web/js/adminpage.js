var randomScalingFactor = function(){ return Math.round(Math.random()*100)};
		var lineChartData = {
			labels : ["2008","2009","2010","2011","2012","2013","2014"],
			datasets : [
				{
					label: "Students",
					fillColor : "rgba(220,220,220,0.2)",
					strokeColor : "rgba(220,220,220,1)",
					pointColor : "rgba(220,220,220,1)",
					pointStrokeColor : "#fff",
					pointHighlightFill : "#fff",
					pointHighlightStroke : "rgba(220,220,220,1)",

					data : [0, 4000,4200,4600,5000,5100,5300,6000]
				}
			]

		}

	window.onload = function(){
		var ctx = document.getElementById("overview").getContext("2d");
		window.myLine = new Chart(ctx).Line(lineChartData, {
			responsive: true
		});
	}
