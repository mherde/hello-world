/*
 * Created on 01.11.2005
 */
package de.unikassel.ir.vsr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a document as map on terms with weights.
 * 
 * @author Christoph Schmitz
 * @author Stephan Doerfel
 */
public interface Document extends Iterable<String> {

	/**
	 * Iterator over all terms
	 * 
	 * @return Iterator, that contains each occurring String <b>exactly
	 *         once</b>.
	 */
	public Iterator<String> iterator();

	/**
	 * Return a documents id
	 * 
	 * @return id
	 */
	public String getId();

	/**
	 * Get document from a given Stream
	 * 
	 * @param input
	 *            the stream of the original document
	 * @throws IOException
	 */
	public void read(InputStream input) throws IOException;

	/**
	 * Return frequency of a given term
	 * 
	 * @param term
	 * @return Number of occurrences of term, 0 if term not in document
	 */
	public int getTermCount(String term);

	/**
	 * Calculation of normalized term frequency of term by normalizing with
	 * maximal frequency of a term in this document
	 * 
	 * @return normalized term frequency of term
	 */
	public double getTF(String term);

	/**
	 * @return length of document vector
	 */
	public double getDocLength();
	
	
	/**
	 * Setting new value of document vector length
	 * @param docLenght: new value for the length of the document vector
	 */
	public void setDocLength(double docLength);

	/**
	 * Return all positions, where a given term occurs (starting with 0, ordered
	 * from earlies to last)
	 * 
	 * @param term
	 * @return positions in the document
	 */
	public ArrayList<Integer> getTermPositions(String term);

	/**
	 * Return the representation of document in a style suitable for humans
	 * 
	 * @return String with terms and their positions
	 */
	public String toString();

	/**
	 * Number of terms (each counted according to its number of occurrences) in
	 * the document
	 * 
	 * @return number of terms in the document
	 */
	public int size();
}