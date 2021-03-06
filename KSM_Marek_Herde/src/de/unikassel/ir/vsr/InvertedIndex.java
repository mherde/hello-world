/*
 * Created on 03.11.2005
 */
package de.unikassel.ir.vsr;

import java.util.Iterator;

/**
 * Interface for an "Inverted Index" on a corpus of Documents.
 * 
 * @author Christoph Schmitz
 * @author Beate Krause
 * @author Stephan Doerfel
 * @author Andreas Schmidt
 */
public interface InvertedIndex {
	
	/**
	 * Return the size of the Corpus (= number of Documents)
	 * @return
	 */
	public int getCorpusSize();

	/**
	 * Return the maximum Frequency of a term in a Document
	 * in the Corpus computed over all Terms and Documents
	 * @return
	 */
	public int getMaxFrequency();
	
	/**
	 * Return the cosine similarity for a query and Documents containing the query terms
	 * in the Corpus ordered descending by similarity score as SearchResultItems
	 * @param query
	 * @return Iterator for SearchResultItems of query
	 */
	public Iterator<? extends SearchResultItem> getCosineSimilarities(String[] query);
	
	/**
	 * Return the TokenInfo for a given term
	 * @param term
	 * @return
	 */
	public TokenInfo getTokenInfo(String term);
	
	/**
	 * Return the index formatted as
	 * 
	 * <pre>
	 * [
	 *   term (idf) -> [ docId -> tfidf,  docId -> tfidf, ... ]
	 *   term (idf) -> [ docId -> tfidf,  docId -> tfidf, ... ]
	 *   term (idf) -> [ docId -> tfidf,  docID -> tfidf, ... ]
	 * ]
	 * </pre>
	 */
	public String toString();
}
