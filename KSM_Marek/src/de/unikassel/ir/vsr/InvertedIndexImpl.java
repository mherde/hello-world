package de.unikassel.ir.vsr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class InvertedIndexImpl implements InvertedIndex {

	/** data structure of the inverted index */
	private HashMap<String, TokenInfo> tokenHash;
	/** reference to corpus containing all documents */
	private Corpus corpus;
	/** save maximal term frequency of a term in a document */
	private int maximalTermFrequency = 0;
	/** inverted index string representation */
	private String invertedIndex = "";

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
				/* cast, because we were not allowed to extend interface */
				DocumentImpl document = (DocumentImpl) tokenOccurence.getDocument();

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

			/* cast, because we were not allowed to extend interface */
			DocumentImpl currentDoc = (DocumentImpl) document;

			double currentDocLength = currentDoc.getDocLength();

			/* length is root of the current stored length */
			currentDoc.setDocLength(Math.sqrt(currentDocLength));
		}
	}

	@Override
	public int getCorpusSize() {
		/* number of documents */
		return this.corpus.size();
	}

	@Override
	public int getMaxFrequency() {

		if (this.maximalTermFrequency == 0) {
			/*
			 * stores the total maximal frequency of a token in corpus' document
			 */
			int maxFrequency = 0;

			/* iteration over all tokens */
			for (String token : tokenHash.keySet()) {

				/*
				 * stores maximal frequency in a corpus' document of the current
				 * token
				 */
				int maxFrequencyOfCurrentTerm = 0;

				/*
				 * getting term count for every occurrences in a document in
				 * order to determine maximal frequency in a corpus' document of
				 * this term
				 */
				for (TokenOccurrence occ : getTokenInfo(token).getTokenOccurrenceList()) {
					/* frequency in the current document of the current token */
					int newFrequency = occ.getDocument().getTermCount(token);
					/* determining whether new frequency is greater or not */
					if (newFrequency > maxFrequencyOfCurrentTerm)
						maxFrequencyOfCurrentTerm = newFrequency;
				}

				/* checking whether current frequency is new maximum */
				if (maxFrequencyOfCurrentTerm > maxFrequency) {
					maxFrequency = maxFrequencyOfCurrentTerm;
				}
			}
			this.maximalTermFrequency = maxFrequency;
		}

		return this.maximalTermFrequency;
	}

	/**
	 * analog to slide 36 + 37, lecture 03
	 */
	@Override
	public Iterator<? extends SearchResultItem> getCosineSimilarities(String[] query) {
		/*
		 * contains all found documents including similarities ordered by their
		 * cosines similarity w.r.t. query
		 */
		SortedSet<SearchResultItem> results = new TreeSet<>();

		/* mapping between document and cosines similarity score */
		HashMap<Document, Double> similaritiesMap = new HashMap<>();

		/* mapping between token and tf-idf values of the query */
		HashMap<String, Double> queryMap = new HashMap<>();

		/* counter for the most frequent token in the query */
		double maxFrequency = 0.;

		/* determining frequency for every token in query */
		for (String token : query) {

			/* ignoring difference between upper and lower case */
			token = token.toLowerCase();

			/* increasing counter of current token */
			double tokenCounter = queryMap.getOrDefault(token, 0.) + 1;

			if (maxFrequency < tokenCounter) {
				/* determining maximal frequency of token in the query */
				maxFrequency = tokenCounter;
			}

			/* adding token with new tokenCounter value */
			queryMap.put(token, tokenCounter);
		}

		/*
		 * determining scalar product of all documents with the query, only
		 * documents with at least one token contained by the query are regarded
		 */
		for (String token : queryMap.keySet()) {

			/* needed values to calculate tf-idf */
			TokenInfo tokenInfo = this.getTokenInfo(token);
			double idf = (tokenInfo.getTokenOccurrenceList().size() != 0) ? tokenInfo.getIdf() : 0.;
			double tokenCounter = queryMap.get(token);
			/* normalizing frequency of every token */
			tokenCounter /= maxFrequency;

			/* calculation of tf-idf value for current token */
			queryMap.put(token, idf * tokenCounter);

			/* iteration over all documents that contain the current token */
			for (TokenOccurrence tokenOccurrence : tokenInfo.getTokenOccurrenceList()) {

				/*
				 * successive calculation of the scalar product between current
				 * document and current token
				 */
				Document document = tokenOccurrence.getDocument();
				double currentScore = similaritiesMap.getOrDefault(document, 0.);
				double newScore = currentScore + queryMap.get(token) * tokenOccurrence.getWeight();
				similaritiesMap.put(document, newScore);
			}
		}

		/* calculation of the query vector length */
		double queryLength = 0.;
		for (double tokenTfIdf : queryMap.values()) {
			queryLength += Math.pow(tokenTfIdf, 2);
		}
		queryLength = Math.sqrt(queryLength);

		/* iteration over all found documents */
		for (Document document : similaritiesMap.keySet()) {
			/* cast, because we were not allowed to extend interface */
			DocumentImpl currentDoc = (DocumentImpl) document;

			/*
			 * calculation of cosines similarity between current document and
			 * query
			 */
			double score = similaritiesMap.get(document);
			if (queryLength > 0)
				score = score / (queryLength * currentDoc.getDocLength());

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
		if (this.invertedIndex.equals("")) {
			/* String representation of inverted index */
			this.invertedIndex = "[\n";

			/* iteration over all tokens */
			for (String token : this.tokenHash.keySet()) {

				/* printing token with its idf and its occurrences */
				TokenInfo info = this.getTokenInfo(token);
				this.invertedIndex += String.format(" %-20s (%.15f) -> [", token, info.getIdf());
				for (TokenOccurrence occ : info.getTokenOccurrenceList()) {
					if (occ != info.getTokenOccurrenceList().last())
						this.invertedIndex += " " + occ.toString() + "; ";
					else
						this.invertedIndex += " " + occ.toString() + " ]\n";
				}
			}

			this.invertedIndex += "]";
		}

		return invertedIndex;
	}

}
