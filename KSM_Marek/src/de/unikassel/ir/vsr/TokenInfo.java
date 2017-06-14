package de.unikassel.ir.vsr;

import java.util.TreeSet;

/**
 * TokenInfo 
 * Belongs to one Token and contains a reference to the token occurrence list and the idf weight
 * 
 * @author Andreas Schmidt
 *
 */
public interface TokenInfo {

	/**
	 * Set the IDF weight for this Token
	 * @return 
	 */
	void setIdf(double idf);
	
	/**
	 * Return the IDF weight for this Token
	 * @return 
	 */
	public double getIdf();

	/**
	 * Return the token occurrence list for this Token
	 * @return 
	 */
	public TreeSet<TokenOccurrence> getTokenOccurrenceList();

}