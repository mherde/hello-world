package de.unikassel.ir.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import de.unikassel.ir.vsr.HTMLDocument;
import junit.framework.TestCase;

/**
 * TestCase for the {@link HTMLDocument}. Checks with a given URL, whether the
 * terms are extracted correctly and whether "alt"-, "title"-texts (etc.) are
 * included
 * 
 * @author chs
 * @version 1 (bkr)
 * @author Stephan Doerfel
 */
public class HTMLDocumentTest extends TestCase {

	private HTMLDocument doc;

	public void setUp() {
		try {
			URL url = new URL("http://www.kde.cs.uni-kassel.de/lehre/ss2013/IR/uebungen/parseTest.html");
			doc = new HTMLDocument(url);
			doc.read(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test whether the frequencies of some examplary terms are correct
	 */
	public void testTerms() {
		// some of these terms occur in "alt-" or "title-" tags
		String[] words = { "simpson", "itchi", "scratchi", "imag", "titl", "show", "mous" };
		int[] freqs = { 2, 8, 8, 1, 1, 8, 1 };

		for (int i = 0; i < words.length; i++) {
			assertEquals(freqs[i], doc.getTermCount(words[i]));
		}
	}

	/**
	 * Test the exact number of words to be extracted
	 */
	public void testSize() {
		int nTerms = 89;

		assertEquals(nTerms, doc.size());
	}

	/**
	 * Tests whether all links where found
	 * 
	 * @throws MalformedURLException
	 */
	public void testLinks() throws MalformedURLException {
		Set<URL> expected = new HashSet<URL>();
		expected.add(new URL("http://en.wikipedia.org/wiki/Itchy_&_Scratchy"));

		assertEquals(expected, doc.getExtractedLinks());
	}
}
