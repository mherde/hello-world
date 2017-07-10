package de.unikassel.ir.spider;

import java.net.URL;

public class AndURLFilter implements URLFilter {

	private final URLFilter filter1;
	private final URLFilter filter2;

	/**
	 * Create a new AndURLFilter.
	 * 
	 * @param filter1
	 *            the first filter.
	 * @param filter2
	 *            the second filter.
	 */
	public AndURLFilter(final URLFilter filter1, final URLFilter filter2) {
		this.filter1 = filter1;
		this.filter2 = filter2;
	}

	@Override
	public boolean shouldInclude(URL from, URL to) {
		return filter1.shouldInclude(from, to) && filter2.shouldInclude(from, to);
	}

}
