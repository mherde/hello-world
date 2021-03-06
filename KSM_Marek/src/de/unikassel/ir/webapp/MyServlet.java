package de.unikassel.ir.webapp;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String corpusPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
	}

	@Override
	public void init() {
		corpusPath = this.getServletContext().getRealPath("resources/texte");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/* getting content from the search field */
		String searchterm = request.getParameter("searchfield");
		
		/* determining the used boolean operator */
		String operator = request.getParameter("operator");
		
		/* printing some information */
		response.getWriter().append("Moogle found the following documents w.r.t. your query:\n");
		
		/* creation of new search engine to search for documents */
		SearchEngine searcher = new SearchEngine();
		
		/* stores the corresponding documents */
		List<String> result = null;
		
		/* checking which booleanOperator is used */
		if (operator.equals("OR")) {
			/* OR operator */
			result = searcher.testQuery(searchterm, false);
		} else if(operator.equals("AND")) {
			/* AND operator */
			result = searcher.testQuery(searchterm, true);
		} else {
			/* RANKED operator */
			result = searcher.testRankedQuery(searchterm);
		}
		
		/* printing all documents */
		for (String s : result) {
			response.getWriter().println(s);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}


}
