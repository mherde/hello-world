/*
 * Created on 01.11.2005
 */
package de.unikassel.ir.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unikassel.ir.vsr.Corpus;
import de.unikassel.ir.vsr.CorpusImpl;
import de.unikassel.ir.vsr.Document;
import de.unikassel.ir.vsr.DocumentImpl;
import junit.framework.TestCase;

/**
 * TestCase for simple boolean queries against a
 * {@link de.unikassel.ir.vsr.Corpus}.
 * 
 * @author Christoph Schmitz
 * @author Stephan Doerfel
 */
public class BooleanTest extends TestCase {
	private Corpus corpus;

	/**
	 * this is executed before the other test methods beging
	 */
	public void setUp() throws FileNotFoundException, IOException {
		corpus = new CorpusImpl();
		int files = 0;
		// TODO: set path for corpus
		File dir = new File("resources/texte");
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				files++;
				FileInputStream stream = new FileInputStream(file);
				Document doc = new DocumentImpl(file.getName());
				doc.read(stream);
				stream.close();
				corpus.addDocument(doc);
			}
		}
		assertEquals(250, files);
	}

	/**
	 * Test for Queries with AND
	 */
	public void testAndQuery() {
		// This query's result should be 1, 5258, 8961 and 13462
		Set<String> expectedIDs = new HashSet<String>();
		for (String id : new String[] { "1", "5258", "8961", "13462" }) {
			expectedIDs.add("Reut_" + id + ".txt");
		}

		Set<String> resultIDs = new HashSet<String>();
		Collection<Document> docs = corpus.getDocumentsContainingAll("cocoa",
				"shipment");
		for (Document doc : docs) {
			resultIDs.add(doc.getId());
		}
		assertEquals(expectedIDs, resultIDs);
	}

	/**
	 * Test for Queries with OR
	 */
	public void testOrQuery() {
		// This query's result should be 49, 2310, 5258, 6657, 12179, 12772, 12924, 13462

		Set<String> expectedIDs = new HashSet<String>();
		for (String id : new String[] { "49", "2310", "5258", "6657", "12179",
				"12772", "12924", "13462" }) {
			expectedIDs.add("Reut_" + id + ".txt");
		}

		Set<String> resultIDs = new HashSet<String>();
		Collection<Document> docs = corpus.getDocumentsContainingAny(
				"alternative", "daily");
		for (Document doc : docs) {
			resultIDs.add(doc.getId());
		}
		assertEquals(expectedIDs, resultIDs);
	}
	
	/**
	 * Test for Term Positions
	 */
	public void testPositions() {
		Set<ArrayList<Integer>> expectedResult = new HashSet<ArrayList<Integer>>();
		String[] tmpResult = "1, 12, 28, 40, 69, 330, 336, 377, 500, 539, 567, 579, 612, 637, 662, 765], [2, 13, 92, 117, 173, 207, 531], [1, 45, 97, 200, 220, 273, 363, 390, 419, 456, 469, 504, 517, 545, 580, 599, 625, 648, 756, 789, 850], [1, 11, 107, 190, 217, 231, 241, 346, 420, 429, 481, 495".split("\\], \\[");
		for (String tmpString : tmpResult) {
			ArrayList<Integer> tmpList = new ArrayList<Integer>();
			System.out.println(tmpString);
			for (String s: tmpString.split(", "))
				tmpList.add(Integer.parseInt(s));
			expectedResult.add(tmpList);
		}

		Set<ArrayList<Integer>> resultPositions = new HashSet<ArrayList<Integer>>();
		Collection<Document> docs = corpus.getDocumentsContainingAll("cocoa",
				"shipment");
		for (Document doc : docs) {
			resultPositions.add(doc.getTermPositions("cocoa"));
		}
		
		assertEquals(expectedResult, resultPositions);
	}
}