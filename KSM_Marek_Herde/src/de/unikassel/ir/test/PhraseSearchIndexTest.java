package de.unikassel.ir.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unikassel.ir.vsr.Corpus;
import de.unikassel.ir.vsr.CorpusImpl;
import de.unikassel.ir.vsr.Document;
import de.unikassel.ir.vsr.DocumentImpl;
import de.unikassel.ir.vsr.PhraseSearchIndex;

import junit.framework.TestCase;

/**
 * Testcase for indices implementing the PhraseSearch interface.
 * Uses the corpus in resources/texte.
 * 
 * @author Jens Illig
 */
public class PhraseSearchIndexTest extends TestCase {
	private Corpus corpus;

	private PhraseSearchIndex index;

	/**
	 * Make a corpus and the index
	 */
	public void setUp() {
		corpus = new CorpusImpl();

		File dir = new File("resources/texte");
		for (File file : dir.listFiles()) {
			try {
				if (!file.isDirectory()) { 
					FileInputStream stream = new FileInputStream(file);
					Document doc = new DocumentImpl(file.getName());
					doc.read(stream);
					stream.close();
					corpus.addDocument(doc);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		index = new PhraseSearchIndex(corpus);
	}

	/**
	 * Test whether a phrase that does not exist in any Document
	 * returns an empty result
	 */
	public void testIndexEmpty() {
		// Search for some phrase that does not exist in any Document
		String phrase = "wer das findet liegt falsch";
		Map<Document, List<Integer>> result = index.searchPhrase(phrase);
		
		// make expected result (empty Map)
		Map<String, int[]> expected = new HashMap<String, int[]>();
		checkResult(result, expected);
	}
	
	/**
	 * Test: Phrase 1
	 */
	public void testIndexNewZealand() {
		// Search for "New Zealand"
		String phrase = "new zealand";
		Map<Document, List<Integer>> result = index.searchPhrase(phrase);
		
		// make Expected result
		Map<String, int[]> expected = new HashMap<String, int[]>();
		expected.put("Reut_4739.txt", new int[] { 3, 23, 84 });
		expected.put("Reut_5734.txt", new int[] { 25 });
		expected.put("Reut_9604.txt", new int[] { 4, 13 });
		expected.put("Reut_8214.txt", new int[] { 9 });
		
		checkResult(result, expected);
	}
	
	
	/**
	 * Test: Phrase 2
	 */
	public void testIndexCommittee() {
		// Search for "executive committee"
		String phrase = "executive committee";
		Map<Document, List<Integer>> result = index.searchPhrase(phrase);
		
		// make Expected result
		Map<String, int[]> expected = new HashMap<String, int[]>();
		expected.put("Reut_10122.txt", new int[] { 245 });
		expected.put("Reut_10760.txt", new int[] { 712 });
		expected.put("Reut_7311.txt", new int[] { 28, 90 });
		expected.put("Reut_7512.txt", new int[] {296 });
	
		checkResult(result, expected);
	}
	
	/**
	 * Test: Context before and after search phrase (5 words each)
	 */
	public void testContextNewZealand() {
		// Search for "new zealand"
		String phrase = "new zealand";
		Map<Document, List<Integer>> result = index.searchPhrase(phrase);

		// Make expectied result (contexts around "new zealand")
		Map<String, List<String>> expected = new HashMap<String, List<String>>();
		expected.put("Reut_4739.txt", Arrays.asList("strike to close", "ports on monday 13 harbour"));
		expected.put("Reut_9604.txt", Arrays.asList("normal work resumes at", "ports 13 normal work has"));
		expected.put("Reut_5734.txt", Arrays.asList("support of pay claims closed", "s 15 ports for 24"));
		expected.put("Reut_8214.txt", Arrays.asList("but further disruption likely 13", "ports reopened at 0730 hrs"));
		
		// check for each found result its context
		for (Document doc: result.keySet()) {			
			List<String> resultContext = index.getContext(phrase, doc, result.get(doc).get(0));
			// check beforePart and afterPart
			for (String context: resultContext){
				assertTrue(expected.get(doc.getId()).contains(context));
			}
		}
	}
	
	

	/**
	 * Compare found result with expected result (can differ => check element by element)
	 * 
	 * @param result = found result
	 * @param expected = expected result
	 */
	public void checkResult(Map<Document, List<Integer>> result,
			Map<String, int[]> expected) {
		// correct number of Documents retrieved?
		assertEquals(expected.size(), result.size());

		// for all Documents: check positions
		for (Document doc : result.keySet()) {
			assertTrue(expected.containsKey(doc.getId()));

			List<Integer> posResult = result.get(doc);
			int[] expResult = expected.get(doc.getId());

			assertEquals(posResult.size(), expResult.length);

			for (int pos : expResult) {
				assertTrue(posResult.contains(pos));
			}
		}

	}
}