/*
 * Created on 30.11.2005
 */
package de.unikassel.ir.vsr;

import java.util.List;


/**
 * TokenOccurrence 
 * Belongs to one Term and contains a reference to a specific Document, the weight of the term w.r.t that Document
 * TokenOccurrences are ordered <em>descending</em> by weight!
 * 
 * @author Christoph Schmitz
 * @author Stephan Doerfel
 */
public interface TokenOccurrence extends Comparable<TokenOccurrence> {

	/**
	 * Return the Document
	 * @return
	 */
	public abstract Document getDocument();

	/**
	 * Return the weight of the Token in this Document
	 * @return 
	 */
	public abstract double getWeight();

	/**
	 * @return String-representation dieses Gewichts
	 */
	public abstract String toString();
	
	/**
	 * Returns the positions where the term occurs in the document
	 * counted ascending from 0
	 * @return
	 */
	public abstract List<Integer> getPositions();

}