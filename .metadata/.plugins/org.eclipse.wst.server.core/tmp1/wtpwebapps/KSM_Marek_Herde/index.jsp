<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moogle</title>
</head>
<body>
	<center>
		<h1>Moogle</h1>
		<%@ page import="de.unikassel.ir.webapp.SearchEngine"%>
		<%@ page import="java.util.List"%>
		<form>
			<input type="text" name="searchfield"> <input type="submit"
				name="search" /> <br> <br> <select name="operator">
				<option>RANK</option>
				<option>OR</option>
				<option>AND</option>
			</select>
		</form>

		<%
			String search = request.getParameter("search");

			if (search != null) {

				/* getting content from the search field */
				String searchterm = request.getParameter("searchfield");

				/* determining the used boolean operator */
				String operator = request.getParameter("operator");

				/* printing some information */
				out.println("Moogle found the following documents w.r.t. your query:");
				out.println("<br/><br/>");

				/* creation of new search engine to search for documents */
				SearchEngine searcher = new SearchEngine();

				/* stores the corresponding documents */
				List<String> result = null;

				/* checking which booleanOperator is used */
				if (operator.equals("OR")) {
					/* OR operator */
					result = searcher.testQuery(searchterm, false);
				} else if (operator.equals("AND")) {
					/* AND operator */
					result = searcher.testQuery(searchterm, true);
				} else {
					/* RANKED operator */
					result = searcher.testRankedQuery(searchterm);
				}

				/* printing all documents */
				for (String s : result) {
					out.println(s);
					out.println("<br/><br/>");
				}
			}
		%>
	</center>
</html>