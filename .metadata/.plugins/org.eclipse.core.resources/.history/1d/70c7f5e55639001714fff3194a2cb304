/*
 * Created on 25.04.2016 
 */
package de.unikassel.ir.vsr;

/**
 * SearchResultItem
 * Represents one item of a search result, i.e. a document with the corresponding similarity score for a query.
 * Note that SearchResultItems are ordered <em>descending</em> by similarity score!
 * 
 * @author Andreas Schmidt
 */
public interface SearchResultItem extends Comparable<SearchResultItem> {

	/**
	 * Return the Document
	 * @return
	 */
	public abstract Document getDocument();

	/**
	 * Return the similarity score of this Document (e.g. cosine similarity)
	 * @return 
	 */
	public abstract double getSimilarityScore();

	/**
	 * Set the similarity score of this Document (e.g. cosine similarity)
	 * @param similarityScore
	 */
	public abstract void setSimilarityScore(double similarityScore);

	/**
	 * Return this search result formatted as
	 * 
	 * <pre>
	 * (docId, similarityScore)
	 * </pre>
	 */
	public abstract String toString();
	
}