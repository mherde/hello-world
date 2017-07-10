package de.unikassel.ir.spider;

import java.net.URL;

public interface URLFilter {

	/**
	 * Main filter function of URLFilter interface.
	 * 
	 * @param from
	 *            original URL
	 * @param to
	 *            URL linked original URL
	 * @return boolean answer of filter
	 */
	boolean shouldInclude(URL from, URL to);

}
