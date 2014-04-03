<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
	<link rel="stylesheet" href="css/style.css" type="text/css" />
	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
	<script type="text/javascript">
	    
		function crunchifyAjax(obj) {
	    	
	    	var api = document.getElementById("api").value;
	    	$.ajax({
	            url : 'ajaxSimSearch.html?repo='+api+'&query='+obj.value,
    		    success : function(data) {
            		$('#result').html(data);
      			}
			});
	    }

	</script>
</head>
<body>
	<div id="background">
		<div id="page">
			<div id="header">
				<div id="logo">
					<h1>Automated Software Engineering Group @ NCSU</h1>
					<h1>Software Engineering RealSearch Group @ NCSU</h1>
				</div>
				<div id="navigation">
					<ul>
						<li class="selected">
							<a href="index.html">Home</a>
						</li>
						<li>
							<a href="about.html">About</a>
						</li>
						<li>
							<a href="contact.html">Contact</a>
						</li>
					</ul>
				</div>
			</div>
			<div id="contents">
				<div class="box">
					<div>
						<div class="body">				
							<form action="searchAPI">
							<h1>Mapping methods across API</h1>
							<input id="api" type="text" name="api" value='${api}' readonly><br>
							<div id="result"></div>
							<ul>
								<c:forEach var="item" items="${message}">
									<li><input id ="repo" type="radio" name="repo" value='${item}' onclick="crunchifyAjax(this);" >${item}</li>
					      		</c:forEach>
					      	</ul>
					      	
					      	</form>
						</div>
					</div>
				</div>
				
			</div>
			<div id="footer">
				<div>
					<ul class="navigation">
						<li class="active">
							<a href="index.html">Home</a>
						</li>
						<li>
							<a href="about.html">About</a>
						</li>
						<li>
							<a href="contact.html">Contact</a>
						</li>
					</ul>
					<div id="connect">
						<a href="#" class="pinterest"></a> <a href="#" class="facebook"></a> <a href="#" class="twitter"></a> <a href="#" class="googleplus"></a>
					</div>
				</div>
				<p>
					&copy; 2013 by RAHUL PANDITA. All Rights Reserved. Template from <a href="http://www.freewebsitetemplates.com/">FREE WEBSITE TEMPLATE</a>
				</p>
			</div>
		</div>
	</div>
</body>
</html>