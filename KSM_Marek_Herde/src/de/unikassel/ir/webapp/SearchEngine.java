package de.unikassel.ir.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import de.unikassel.ir.spider.Crawler;
import de.unikassel.ir.vsr.Corpus;
import de.unikassel.ir.vsr.CorpusImpl;
import de.unikassel.ir.vsr.Document;
import de.unikassel.ir.vsr.DocumentImpl;
import de.unikassel.ir.vsr.PhraseSearchIndex;
import de.unikassel.ir.vsr.SearchResultItem;
import de.unikassel.ir.vsr.TokenInfo;
import de.unikassel.ir.vsr.TokenOccurrence;

/**
 * Search Engine Bean to use the query methods in JSPs
 * 
 * @author Beate Krause
 */
public class SearchEngine {

	/**
	 * The corpus
	 */
	private Corpus corpus;
	/**
	 * inverted index
	 */
	private static PhraseSearchIndex index = initIndex();
	/**
	 * corpus of crawled websites
	 */
	private static Corpus webCorpus;

	/**
	 * Creating of web corpus with help of crawler.
	 */
	private static PhraseSearchIndex initIndex() {
		org.apache.log4j.Logger log = Logger.getLogger(SearchEngine.class);
		BasicConfigurator.configure();
		if (index == null) {
			try {
				log.debug("Start...");
				// Create a Crawler, Start it and give it a URL to start with
				Crawler spider = new Crawler(100, 10, 20);

				URL url = new URL("https://www.w3schools.com/html/");

				log.debug("Spider initialized\n----------------------------------------------");
				log.debug("Pushing " + url);

				spider.addURL(null, url);

				// Wait until all the nPages have been downloaded and processed
				spider.waitUntilDone();

				// Return corpus.
				webCorpus = spider.getDocumentsAsCorpus();

				for (Document doc : webCorpus) {
					log.debug("Doc: " + doc.getId());
				}
				return new PhraseSearchIndex(webCorpus);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				System.err.println("ERROR: COULD NOT LOAD CORPUS.");
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Constructor, sets the corpus
	 */
	public SearchEngine() {
		/* check availability of web corpus */
		if (index != null) {
			this.corpus = webCorpus;
		} else {
			/* else load a default corpus */
			this.corpus = new CorpusImpl();
			loadDefaultCorpus();
		}
	}

	/**
	 * Load Texts into the corpus
	 */
	public void loadDefaultCorpus() {
		File dir = new File(MyServlet.corpusPath);

		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				FileInputStream stream;
				try {
					stream = new FileInputStream(file);
					Document doc = new DocumentImpl(file.getName());
					doc.read(stream);
					stream.close();
					corpus.addDocument(doc);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Proceeding of query.
	 * 
	 * @param terms:
	 *            query
	 * @param mode:
	 *            query type [RANKED, AND, OR, PHRASE]
	 * @return documents and context matching query terms
	 */
	public Map<String, List<String>> query(String terms, String mode) {
		/* stores result mapping between documents and contexts */
		Map<String, List<String>> results = new HashMap<>();

		/* preprocessing query */
		String[] query = terms.toLowerCase().trim().replaceAll("[^A-Za-z0-9 -]", "").split("\\s+|-");
		/* printing preprocessed query */
		System.out.println("Query: " + Arrays.toString(query));

		/* proceeding query depending on its type */
		if (query.length > 0) {
			switch (mode) {
			case "OR":
				results = this.testOrQuery(query);
				break;
			case "AND":
				results = this.testAndQuery(query);
				break;
			case "RANK":
				results = this.testRankedQuery(query);
				break;
			case "PHRASE":
				System.out.println(terms);
				results = this.testPhraseQuery(terms);
				break;
			}
		}
		return results;
	}

	/**
	 * Determines mapping between documents that contain all terms of query and
	 * the contexts of these query terms.
	 * 
	 * @param query
	 * @return mapping between documents and context of query terms
	 */
	public Map<String, List<String>> testAndQuery(String[] query) {
		/* stores result mapping between documents and contexts */
		Map<String, List<String>> results = new HashMap<>();

		/* elimination of duplicated terms */
		Set<String> set = new HashSet<String>(Arrays.asList(query));
		String[] uniqueTerms = set.toArray(new String[0]);

		/*
		 * determining documents containing all terms of query by using
		 * intersection operator
		 */
		Set<Document> intersect = null;
		for (String term : uniqueTerms) {
			TreeSet<TokenOccurrence> occurrences = index.getTokenInfo(term).getTokenOccurrenceList();
			Set<Document> docs = new HashSet<>();
			for (TokenOccurrence occ : occurrences) {
				docs.add(occ.getDocument());
			}
			if (intersect == null) {
				intersect = docs;
			} else {
				intersect.retainAll(docs);
			}
		}

		/* determining context of every term in corresponding document */
		if (intersect != null) {
			for (Document doc : intersect) {
				List<String> contexts = new ArrayList<String>();
				for (String term : uniqueTerms) {
					List<Integer> positions = doc.getTermPositions(term);
					for (int pos : positions) {
						List<String> contextList = index.getContext(term, doc, pos);
						String context = contextList.get(0) + " <b>" + term + "</b> " + contextList.get(1);
						contexts.add(context);
					}
				}
				results.put(doc.getId(), contexts);

			}
		}

		return results;

	}

	/**
	 * Determines mapping between documents that contain at least one term of
	 * query and the contexts of these query terms.
	 * 
	 * @param query
	 * @return mapping between found document and context of query terms
	 */
	public Map<String, List<String>> testOrQuery(String[] query) {
		/* stores result mapping between documents and contexts */
		Map<String, List<String>> results = new HashMap<>();

		/* elimination of duplicated terms */
		Set<String> set = new HashSet<String>(Arrays.asList(query));
		String[] uniqueTerms = set.toArray(new String[0]);

		/*
		 * adding every document to map that contains at least on term of query
		 * and creating context for every found query term in a document
		 */
		for (String term : uniqueTerms) {
			TokenInfo info = index.getTokenInfo(term);
			for (TokenOccurrence occ : info.getTokenOccurrenceList()) {
				List<String> contexts = results.getOrDefault(occ.getDocument().getId(), new ArrayList<String>());
				for (int pos : occ.getPositions()) {
					List<String> contextList = index.getContext(term, occ.getDocument(), pos);
					String context = contextList.get(0) + " <b>" + term + "</b> " + contextList.get(1);
					contexts.add(context);
				}
				results.put(occ.getDocument().getId(), contexts);
			}
		}

		return results;
	}

	/**
	 * calculates mapping between documents ranked regarding the cosines
	 * similarity w.r.t. query and context of found query terms
	 * 
	 * @param terms
	 * @return ranked list of documents and context of the found query terms
	 */
	public Map<String, List<String>> testRankedQuery(String[] query) {
		/* stores result mapping between documents and contexts */
		Map<String, List<String>> results = new HashMap<String, List<String>>();

		/*
		 * iterator over all found documents ordered by their cosines similarity
		 */
		Iterator<? extends SearchResultItem> searchResults = index.getCosineSimilarities(query);

		/* adding found documents and contexts to result map */
		while (searchResults.hasNext()) {
			List<String> contexts = new ArrayList<>();
			SearchResultItem item = searchResults.next();
			for (String term : query) {
				List<Integer> positions = item.getDocument().getTermPositions(term);
				if (positions != null)
					for (int pos : positions) {
						List<String> contextList = index.getContext(term, item.getDocument(), pos);
						String context = contextList.get(0) + " <b>" + term + "</b> " + contextList.get(1);
						contexts.add(context);
					}
			}
			results.put(item.getDocument().getId(), contexts);
		}

		return results;
	}

	/**
	 * Calculates mapping between documents ranked containing the given phrase
	 * and determines also contexts of found phrase in a document.
	 * 
	 * @param terms
	 * @return ranked list of documents and context of the found query terms
	 */
	public Map<String, List<String>> testPhraseQuery(String phrase) {
		/* stores result mapping between documents and contexts */
		Map<String, List<String>> results = new HashMap<String, List<String>>();

		/*
		 * mapping of all found documents and the positions of the phrase
		 */
		Map<Document, List<Integer>> searchResults = index.searchPhrase(phrase);

		/* adding found documents and contexts to result map */
		for (Entry<Document, List<Integer>> entry : searchResults.entrySet()) {
			List<String> contexts = new ArrayList<String>();
			for (int pos : entry.getValue()) {
				List<String> contextList = index.getContext(phrase, entry.getKey(), pos);
				String context = contextList.get(0) + " <b>" + phrase + "</b> " + contextList.get(1);
				contexts.add(context);
			}

			results.put(entry.getKey().getId(), contexts);
		}

		return results;

	}

	/**
	 * Query for terms in the corpus
	 * 
	 * @param terms
	 *            query
	 * @param andOperator
	 *            AND or OR query
	 * @return set of document ids
	 */
	public List<String> testQuery(String terms, boolean andOperator) {

		String[] termsRequest = terms.split("\\s+");

		List<String> resultIDs = new ArrayList<String>();
		Collection<Document> docs;

		if (andOperator) {
			docs = corpus.getDocumentsContainingAll(termsRequest);
		} else {
			docs = corpus.getDocumentsContainingAny(termsRequest);
		}

		for (Document doc : docs) {
			resultIDs.add(doc.getId());
		}

		return resultIDs;
	}

	/**
	 * @return the Corpus
	 */
	public Corpus getCorpus() {
		return corpus;
	}
}