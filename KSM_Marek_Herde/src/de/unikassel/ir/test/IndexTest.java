/*
 * Created on 03.11.2005
 */
package de.unikassel.ir.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import de.unikassel.ir.vsr.Corpus;
import de.unikassel.ir.vsr.CorpusImpl;
import de.unikassel.ir.vsr.Document;
import de.unikassel.ir.vsr.DocumentImpl;
import de.unikassel.ir.vsr.InvertedIndexImpl;
import de.unikassel.ir.vsr.SearchResultItem;
import de.unikassel.ir.vsr.TokenOccurrence;

public class IndexTest extends TestCase {
	private InvertedIndexImpl index;

	/**
	 * Test setUp (is executed before the other test methods
	 */
	public void setUp() {
		// Create the Corpus
		Corpus corpus = new CorpusImpl();
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
		// Create the Inverted Index over the Corpus
		index = new InvertedIndexImpl(corpus);
		
	}

	/**
	 * Test the results for the three terms: "november", "shipbuilding" and
	 * "sugarcane"
	 */
	public void testTokenWeights() {
		// Set expected values: one Map for each term containing
		// the documents (that include the term)
		// and the corresponding tf-idf-weights
		Map<String, Double> novemberResult = new HashMap<String, Double>();
		Map<String, Double> shipbuildingResult = new HashMap<String, Double>();
		Map<String, Double> sugarcaneResult = new HashMap<String, Double>();
		// one map to hold the three results
		Map<String, Map<String, Double>> results = new HashMap<String, Map<String, Double>>();
		// term "november": put (document, tf-idf) and add to result
		novemberResult.put("Reut_8683.txt", 1.7877753844034665);
		novemberResult.put("Reut_3639.txt", 1.0215859339448379);
		novemberResult.put("Reut_5175.txt", 0.7151101537613866);
		novemberResult.put("Reut_11173.txt", 0.7151101537613866);
		novemberResult.put("Reut_10302.txt", 0.3972834187563259);
		novemberResult.put("Reut_4898.txt", 0.21032651581217252);
		novemberResult.put("Reut_4470.txt", 0.07151101537613866);
		results.put("november", novemberResult);

		// term "shipbuilding": put (document, tf-idf) and add to result
		shipbuildingResult.put("Reut_5818.txt", 1.1918502562689777);
		shipbuildingResult.put("Reut_3046.txt", 1.1918502562689777);
		shipbuildingResult.put("Reut_1902.txt", 0.9751502096746181);
		shipbuildingResult.put("Reut_11251.txt", 0.4469438461008666);
		shipbuildingResult.put("Reut_4640.txt", 0.27504236683130256);
		shipbuildingResult.put("Reut_6541.txt", 0.26816630766051996);
		shipbuildingResult.put("Reut_677.txt", 0.13242780625210862);
		results.put("shipbuilding", shipbuildingResult);

		// term "sugarcane": put (document, tf-idf) and add to result
		sugarcaneResult.put("Reut_11173.txt", 2.237820869180515);
		sugarcaneResult.put("Reut_259.txt", 1.2432338162113972);
		sugarcaneResult.put("Reut_10306.txt", 0.27290498404640423);
		sugarcaneResult.put("Reut_4630.txt", 0.23310634053963697);
		sugarcaneResult.put("Reut_11330.txt", 0.1434500557166997);
		sugarcaneResult.put("Reut_9069.txt", 0.10360281801761642);
		results.put("sugarcane", sugarcaneResult);

		// The actual testing:
		// Check for each of the three terms
		// that the index returns the expected tf-idf values
		for (String term : results.keySet()) {
			// the expected values
			Map<String, Double> expectedResults = results.get(term);
			// init tf-idf-weight of the last tested Document
			double lastWeight = -1d;
			// go through documents, that the index returns for this term and
			// compare
			for (TokenOccurrence tokenOcc : index.getTokenInfo(term).getTokenOccurrenceList()) {
				// get Document and tf-idf weight
				String docId = tokenOcc.getDocument().getId();
				double weight = tokenOcc.getWeight();
				// get expected tf-idf-value for this Document
				double expectedWeight = expectedResults.get(docId);
				// check if tf-idf are equal (up to 1E-7)
				assertEquals(expectedWeight, weight, 1E-7);

				// Check that the weights are sorted descending:
				// compare weight with previous weight
				// (exept for the first Document, that has no previous
				if (lastWeight > 0d) {
					assertTrue(weight - lastWeight < 1e-7);
				}
				lastWeight = weight;
			}
		}
	}

	/**
	 * Test the results for the three terms: "november", "shipbuilding" and
	 * "sugarcane"
	 */
	public void testCosineSimilarities() {
		// Set expected values: one Map for each term containing
		// the documents (that include the term)
		// and the corresponding cosine similarity
		Map<String[], Map<String, Double>> results = new HashMap<String[], Map<String, Double>>();

		Map<String, Double> novemberRainList = new HashMap<String, Double>();
		novemberRainList.put("Reut_8683.txt", 0.06547245225412755);
		novemberRainList.put("Reut_3110.txt", 0.04246272028820126);
		novemberRainList.put("Reut_10388.txt", 0.03214467610602151);
		novemberRainList.put("Reut_10687.txt", 0.03043787914463984);
		novemberRainList.put("Reut_11173.txt", 0.027413262529167228);
		novemberRainList.put("Reut_3639.txt", 0.0259871390449909);
		novemberRainList.put("Reut_5175.txt", 0.019718311643786953);
		novemberRainList.put("Reut_10302.txt", 0.011239437636958565);
		novemberRainList.put("Reut_4898.txt", 0.007370123040628567);
		novemberRainList.put("Reut_4470.txt", 0.003113417627966361);
		results.put(new String[] { "november", "rain" }, novemberRainList);

		Map<String, Double> alternativeDailyList = new HashMap<String, Double>();
		alternativeDailyList.put("Reut_49.txt", 0.027143228032190228);
		alternativeDailyList.put("Reut_12924.txt", 0.027143228032190228);
		alternativeDailyList.put("Reut_12179.txt", 0.027143228032190228);
		alternativeDailyList.put("Reut_2310.txt", 0.018023857550476988);
		alternativeDailyList.put("Reut_12772.txt", 0.01500020496515776);
		alternativeDailyList.put("Reut_13462.txt", 0.008550116830139922);
		alternativeDailyList.put("Reut_6657.txt", 0.00356441959020627);
		alternativeDailyList.put("Reut_5258.txt", 0.002617716595526957);
		results.put(new String[] { "alternative", "daily" }, alternativeDailyList);

		Map<String, Double> goGoGadgetList = new HashMap<String, Double>();
		goGoGadgetList.put("Reut_857.txt", 0.03032277600515603);
		goGoGadgetList.put("Reut_4735.txt", 0.01567101449005963);
		goGoGadgetList.put("Reut_6493.txt", 0.01462628019072232);
		goGoGadgetList.put("Reut_6998.txt", 0.014345005571669968);
		goGoGadgetList.put("Reut_4470.txt", 0.010331582960205516);
		goGoGadgetList.put("Reut_6657.txt", 0.0034791991125318954);
		results.put(new String[] { "go", "go", "gadget" }, goGoGadgetList);

		for (String[] query : results.keySet()) {
			// the expected values
			Map<String, Double> expectedResults = results.get(query);
			// init tf-idf-weight of the last tested Document
			double lastWeight = -1d;

			// go through documents, that the index returns for this term and
			// compare
			Iterator<? extends SearchResultItem> searchResults = index.getCosineSimilarities(query);


			while (searchResults.hasNext()) {
				SearchResultItem result = searchResults.next();
				// get Document and tf-idf weight
				String docId = result.getDocument().getId();
				double weight = result.getSimilarityScore();
				// get expected tf-idf-value for this Document
				double expectedWeight = expectedResults.get(docId);
				// check if tf-idf are equal (up to 1E-7)
				assertEquals(expectedWeight, weight, 1E-7);

				// Check that the weights are sorted descending:
				// compare weight with previous weight
				// (except for the first Document, that has no previous
				if (lastWeight > 0d) {
					assertTrue(weight - lastWeight < 1e-7);
				}
				lastWeight = weight;
			}
		}
	}
}