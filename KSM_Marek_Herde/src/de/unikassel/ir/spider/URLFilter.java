package de.unikassel.ir.spider;

import java.net.URL;

public interface URLFilter {

	boolean shouldInclude(URL from, URL to);

}
