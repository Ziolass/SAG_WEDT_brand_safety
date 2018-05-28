<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<head>
    <title>Customer Information Center</title>
    <script src="/js/plotly-latest.min.js" type="text/javascript"></script>

    <script type="text/javascript">
        function plotChart(elementId, data, layout) {
            Plotly.newPlot(document.getElementById(elementId), data, layout, {displayModeBar: false});
        }
    </script>
</head>

<body>

<div style="background:#ffffee; text-align:center; padding-bottom:2px">
<h1>Customer Information Center</h1>

Draw customer information graph.
<p/>

<form action="customers" method="get">

    Results size:
    <select name="size">
      <option value="5">5</option>
      <option value="10">10</option>
    </select>
    <p/>
    <button style="padding:5px">Draw Chart</button>
</form>
</div>

<c:if test="${not empty statistics}">

<div style="height:4px; width:100%; background: #eeeeee"></div>

<h2 style="text-align:center">Age and Sales Count Chart</h2>

<div id="customersChart" ></div>

<script>

    var statisticsTextClassifierActors = {
          name: 'Age',
          type: 'lines+markers',
          line: { width: 3},
          marker: { size: 4}
        };
    var statisticsFrontendActors = {
          name: 'Sales Count',
          type: 'lines+markers',
          line: { width: 3},
          marker: { size: 4}
        };
    var tC_X = new Array();
    var tC_Y = new Array();
    var frontend_X = new Array();
    var frontend_Y = new Array();

    <c:forEach items="${statistics}" var="customer" varStatus="i">
        tC_X[${i.index}] = "${statistics.time}";
        tC_Y[${i.index}] = "${statistics.frontendActors}";

        frontend_X[${i.index}] = "${statistics.time}";
        frontend_Y[${i.index}] = "${statistics.textClassifierActors}";
    </c:forEach>

    statisticsTextClassifierActors.x = tC_X;
    statisticsTextClassifierActors.y = tC_Y;

    statisticsFrontendActors.x = frontend_X;
    statisticsFrontendActors.y = frontend_Y;

    var data = [statisticsTextClassifierActors, statisticsFrontendActors];
    var layout = {
        xaxis: {
            title: 'ID',
            showgrid: true,
            zeroline: true,
        },
        yaxis: {
            showgrid: true,
            showline: true,
            zeroline: true,
        }
    };

plotChart("customersChart", data, layout);
</script>
</c:if>

</body>
</html>
