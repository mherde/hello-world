<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="de.unikassel.ir.webapp.SearchEngine"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moogle</title>
</head>
<body>
	<center>
		<h1>Moogle</h1>
		<form method="GET" name="search" action="MyServlet">
			<input type="text" name="searchfield"> <input type="submit"
				name="search" /> <br> <br> <select name="operator">
				<option>RANK</option>
				<option>OR</option>
				<option>AND</option>
				<option>PHRASE</option>
			</select>
		</form>

		<div id="result">
			<pre>
        ${requestScope.sites}
    </pre>
		</div>
	</center>
</html>