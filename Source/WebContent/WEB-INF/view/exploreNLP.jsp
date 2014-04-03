<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Search REST API</title>
        <style type="text/css">
       		div.parserOutput { padding-left: 3em; 
                          padding-top: 1em; padding-bottom: 0px; 
                          margin: 0px; }
       		div.parserOutputMonospace { 
                          padding-top: 1em; padding-bottom: 1em; margin: 0px; 
                          font-family: monospace; padding-left: 3em; }
       			.spacingFree { padding: 0px; margin: 0px; }
    	</style>
        <link rel="stylesheet" href="css/style.css" type="text/css" />
    </head>
    <body>
    
    	<form action="exploreNLP">
		<table>
			<tr>
				<td>
					Query:
				</td>
			</tr>
			<tr>
				<td>
					 <textarea name="search" style="width:400px;height:8em" rows="31" cols="7">${searchstr}</textarea>
				</td>
			</tr>
			<tr>
				<td>
					<input type="submit" value="Submit">
				</td>
			</tr>
		</table>
		<div style="clear: left"> </div>
		<table border="1">
		<tr>
			<td><b>Sentence</b></td>
			<td><b>POS-Tree</b></td>
			<td><b>Stanford-Typed Dependencies</b></td>
			<td><b>Tuple Representation</b></td>
		</tr>
		
		
		<c:forEach var="item" items="${message}">
		
        <tr>
			<td>
${item.sentnce} 
        	</td>
			<td>
				<div class="parserOutput">
        			<pre id="parse" class="spacingFree">
${item.posTree} 
	        		</pre>
        		</div>
        	</td>
        	<td>
				<div class="parserOutput">
        			<pre id="parse" class="spacingFree">
${item.stanfordTypedDependecy} 
	        		</pre>
        		</div>
        	</td>
			<td>
				<div class="parserOutput">
        			<pre id="parse" class="spacingFree">
${item.tuple}
	        		</pre>
        		</div>
        	</td>
		</tr>
        </c:forEach>
        </table>
<hr />        

<div style="clear: left"> </div>
		<table border="1">
		<tr>
			<td><b>CO-Reference Resolution</b></td>
		</tr>
		
		
		<c:forEach var="item" items="${coref}">
		
        <tr>
			<td>
${item} 
        	</td>
		</tr>
        </c:forEach>
        </table>
		</form>
    </body>
</html>