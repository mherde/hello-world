<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="de.unikassel.ir.webapp.SearchEngine"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MOOGLE</title>
</head>
<body>
	<h1>MOOGLE</h1>
	<form method="GET" name="search" action="MyServlet">
	SEARCH <input type="text" name="searchfield"> <input type="submit"
			name="search" /> QUERY TYPE <select
			name="operator">
			<option>RANK</option>
			<option>OR</option>
			<option>AND</option>
			<option>PHRASE</option>
		</select>
	</form>
	<br/>
	<hr size="5" noshade>
	<div id="result">
	<pre>
        ${requestScope.sites}
    </pre>
	</div>
</html>