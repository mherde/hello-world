package de.unikassel.ir.webapp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	public static String stopWordsPath;
	public static String jspPath;
	private SearchEngine searchEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
	}

	@Override
	public void init() {
		corpusPath = this.getServletContext().getRealPath("resources/texte");
		stopWordsPath = this.getServletContext().getRealPath("resources/englishST.txt");
		jspPath = this.getServletContext().getRealPath("index.jsp");
		this.searchEngine = new SearchEngine();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		String search = request.getParameter("search");

		if (search != null) {

			/* getting content from the search field */
			String searchterm = request.getParameter("searchfield");

			/* determining the used boolean operator */
			String operator = request.getParameter("operator");

			StringBuffer sites = new StringBuffer();

			/*
			 * stores the corresponding documents and the context of the query
			 * terms
			 */
			Map<String, List<String>> results = this.searchEngine.query(searchterm, operator);

			/* printing found documents and contexts */
			sites.append("<br/>");
			int i = 1;
			for (Entry<String, List<String>> entry : results.entrySet()) {
				sites.append("<b>");
				sites.append(i++);
				sites.append(".</b>");
				sites.append("<a href=" + entry.getKey() + ">" + entry.getKey() + "</a><br/>");
				for (String context : entry.getValue()) {
					sites.append(context);
					sites.append("<br/>");
				}
				sites.append("<br/>");
			}

			request.setAttribute("sites", sites);
			request.getRequestDispatcher("index.jsp").forward(request, response);
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
