<html>
<head>
<title>Home</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
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
							<h1>Mapping methods across API</h1>
							<p>
								API mapping across different platforms/ languages is highly desirable. Among other reasons, they promote machine handled migration from one API to another. This is very relevant in current context as developers increasingly release different versions of their application either to address a business requirement or to survive in competing market. Given a typical platform(/language) expose a large number of API's for developers to reuse, manually writing these mappings is prohibitively resource intensive and may result in manual error. In this paper we present a promising approach to automatically infer such mappings. In particular, unlike existing approaches in literature that rely on existence of manually ported (or at least functionally similar) software across source and target API's, we use textual description present in API documents to mappings. Furthermore, we also compare our results with these approaches and provide relevant discussions how our approach compliments them.
							</p>
							<form action="exploreAPI">
								<ul>
												<li>
													<input id ="repo" type="radio" name="repo" value='EXPLOREANDROID'> EXPLORE ANDROID API
												</li>
												<li>
													<input id ="repo" type="radio" name="repo" value='EXPLORECLDC'> EXPLORE J2ME CLDC API
												</li>
												<li>
													<input id ="repo" type="radio" name="repo" value='EXPLOREMIDP'> EXPLORE J2ME MIDP API
												</li>
												<li>EXPLORE iOS API <font color="red" size="-2">(comming soon)</font></li>
												<li>EXPLORE Windows Phone API <font color="red" size="-2">(comming soon)</font></li>
												<li>EXPLORE JAVA API <font color="red" size="-2">(comming soon)</font></li>
												<li>EXPLORE C# API <font color="red" size="-2">(comming soon)</font></li>
												
												<li>
													<input type="submit" value="Submit">
												</li>
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
</body>
</html>