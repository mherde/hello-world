package de.unikassel.ir.vsr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class InvertedIndexImpl implements InvertedIndex {

	/** data structure of the inverted index */
	private HashMap<String, TokenInfo> tokenHash;
	private Corpus corpus;

	/**
	 * creation of an index given a corpus of documents
	 * 
	 * @param corpus:
	 *            contains all documents
	 */
	public InvertedIndexImpl(Corpus corpus) {
		/* creating of hash map that stores inverted index */
		this.tokenHash = new HashMap<>();

		/* reference to corpus containing all documents */
		this.corpus = corpus;

		/* processing index creation */
		createInvertedIndex();
		calculateIDF();
		calculateDocumentLengths();
	}

	/**
	 * creates an inverted index by filling the map tokenHash (analog to slide
	 * 28, lecture 03)
	 */
	private void createInvertedIndex() {

		/* iteration over all documents */
		for (Document currentDoc : this.corpus) {

			/* iteration over all tokens in the current document */
			for (String currentToken : currentDoc) {

				/*
				 * getting already existing token info or creating new token
				 * info
				 */
				TokenInfo tokenInfo = this.tokenHash.getOrDefault(currentToken, new TokenInfoImpl());

				/*
				 * adding a new token occurrence to the list of all occurrences
				 * of the current token
				 */
				tokenInfo.getTokenOccurrenceList().add(new TokenOccurrenceImpl(currentDoc, tokenInfo, currentToken));

				/*
				 * adding current token together with its info to the hash map
				 * representing inverted index
				 */
				tokenHash.put(currentToken, tokenInfo);
			}
		}
	}

	/**
	 * calculation of idf for every token (analog to to slide 29, lecture 03)
	 */
	private void calculateIDF() {

		/* iteration over all tokens */
		for (String token : tokenHash.keySet()) {

			/* getting tokenInfo */
			TokenInfo tokenInfo = this.getTokenInfo(token);

			/* number of token's document frequency */
			double occurrences = tokenInfo.getTokenOccurrenceList().size();

			/* calculating and setting inverted documented frequency */
			tokenInfo.setIdf(Math.log(this.getCorpusSize() / occurrences));
		}
	}

	/**
	 * calculation of the lengths of all documents' vectors (analog to slide 31,
	 * lecture 03)
	 */
	private void calculateDocumentLengths() {

		/* iteration over all tokens */
		for (String token : tokenHash.keySet()) {

			/* getting token info of the current token */
			TokenInfo tokenInfo = this.getTokenInfo(token);

			/* iteration over all documents that contain current token */
			for (TokenOccurrence tokenOccurence : tokenInfo.getTokenOccurrenceList()) {

				/* getting current document */
				Document document = tokenOccurence.getDocument();

				/* getting last value for the length of this document */
				double currentDocLenght = document.getDocLength();

				/* increasing document's length */
				double newDocLenght = currentDocLenght + Math.pow(tokenOccurence.getWeight(), 2);

				/* setting new document's length */
				document.setDocLength(newDocLenght);
			}
		}

		/* iteration over all documents in the corpus */
		for (Document document : this.corpus) {

			double currentDocLength = document.getDocLength();

			/* length is root of the current stored length */
			document.setDocLength(Math.sqrt(currentDocLength));

		}
	}

	@Override
	public int getCorpusSize() {
		/* number of documents */
		return this.corpus.size();
	}

	// TODO
	@Override
	public int getMaxFrequency() {
		/* stores the maximal frequency of token in corpus */
		int maxFrequency = 0;

		/* iteration over all tokens */
		for (String token : tokenHash.keySet()) {

			/* stores total frequency in corpus of the current token */
			int currentFrequency = 0;

			/*
			 * adding term count for every occurrences in a document to
			 * currentFrequency
			 */
			for (TokenOccurrence occ : getTokenInfo(token).getTokenOccurrenceList()) {
				currentFrequency += occ.getDocument().getTermCount(token);
			}

			/* checking whether current frequency is new maximum */
			if (currentFrequency > maxFrequency) {
				maxFrequency = currentFrequency;
			}
		}

		return maxFrequency;
	}

	@Override
	public Iterator<? extends SearchResultItem> getCosineSimilarities(String[] query) {
		/*
		 * contains all found documents including scores ordered by their
		 * cosines similarity w.r.t. query
		 */
		SortedSet<SearchResultItem> results = new TreeSet<>();

		/* mapping between document and cosines similarity score */
		HashMap<Document, Double> scoreMap = new HashMap<>();

		/* mapping between token and tf-idf values of the query */
		HashMap<String, Double> queryMap = new HashMap<>();

		/* counter for the most frequent token in the query */
		double maxFrequency = 0;

		/* determining frequency for every token in query */
		for (String token : query) {
			double currentTokenCounter = queryMap.getOrDefault(token, 0.);
			queryMap.put(token, ++currentTokenCounter);
		}

		/* determining maximal frequency of a token in the query */
		for (String token : query) {
			if (queryMap.get(token) > maxFrequency) {
				maxFrequency = queryMap.get(token);
			}
		}

		/* normalizing frequency of every token */
		for (String token : query) {
			double tokenCounter = queryMap.get(token);
			queryMap.put(token, tokenCounter / maxFrequency);
		}

		/*
		 * determining scalar product of all documents with the query, only
		 * documents with at least one token contained by the query are regarded
		 */
		for (String token : queryMap.keySet()) {

			/* needed values to calculate tf-idf */
			TokenInfo tokenInfo = this.getTokenInfo(token);
			double idf = tokenInfo.getIdf();
			double queryTF = queryMap.get(token);

			/* calculation of tf-idf value for current token */
			queryMap.put(token, idf * queryTF);

			/* iteration over all documents that contain the current token */
			for (TokenOccurrence tokenOccurrence : tokenInfo.getTokenOccurrenceList()) {

				/*
				 * successive calculation of the scalar product between current
				 * document and current token
				 */
				Document document = tokenOccurrence.getDocument();
				double currentScore = scoreMap.getOrDefault(document, 0.);
				double newScore = currentScore + queryMap.get(token) * tokenOccurrence.getWeight();
				scoreMap.put(document, newScore);
			}
		}

		/* calculation of the query vector length */
		double queryLength = 0.;
		for (double tokenTF_IDF : queryMap.values()) {
			queryLength += Math.pow(tokenTF_IDF, 2);
		}
		queryLength = Math.sqrt(queryLength);

		/* iteration over all found documents */
		for (Document document : scoreMap.keySet()) {

			/*
			 * calculation of cosines similarity between current document and
			 * query
			 */
			double score = scoreMap.get(document);
			score = score / (queryLength * document.getDocLength());

			/* adding document and its similarity score to result list */
			SearchResultItem result = new SearchResultItemImpl(document, score);
			results.add(result);
		}

		return results.iterator();
	}

	@Override
	public TokenInfo getTokenInfo(String term) {

		/*
		 * return token info present, otherwise creation of an empty token info
		 */
		return this.tokenHash.getOrDefault(term, new TokenInfoImpl());
	}

	@Override
	public String toString() {
		/* String representation of inverted index */
		String s = "";

		/* iteration over all tokens */
		for (String token : this.tokenHash.keySet()) {

			/* printing token with its idf and its occurrences */
			TokenInfo info = this.getTokenInfo(token);
			s += token + " (" + info.getIdf() + ") " + " -> [";
			for (TokenOccurrence occ : info.getTokenOccurrenceList()) {
				s += " " + occ.toString() + " ";
			}
			s += "]\n";

		}

		return s;
	}

}