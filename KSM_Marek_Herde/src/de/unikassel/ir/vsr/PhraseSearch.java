package de.unikassel.ir.vsr;

import java.util.List;
import java.util.Map;

import de.unikassel.ir.vsr.Document;

/**
 * Interface for indices with phrase search support
 * 
 * @author Jens Illig
 */
public interface PhraseSearch {
	
	
	/**
	 * Searches for all Documents containing a phrase in the corpus and the positions of the Phrase in the Document.
	 * The first Position in a Document is 0.
	 * 
	 * @param phrase = the phrase as a List of Words
	 * @return Map of Documents to positions (where phrase was found) or an empty Map (no results)
	 */
	public Map<Document, List<Integer>> searchPhrase(List<String> phrase);
	
	/**
	 * Searches for all Documents containing a phrase in the corpus and the positions of the Phrase in the Document.
	 * The first Position in a Document is 0.
	 * 
	 * @param phrase = the phrase as one String
	 * @return Map of Documents to positions (where phrase was found) or an empty Map (no results)
	 */
	public Map<Document, List<Integer>> searchPhrase(String phrase);
	
	/**
	 * Returns the Context of a phrase. 
	 * 
	 * @param Phrase = the phrase as a String
	 * @param doc = Document to retrieve the context from
	 * @param pos = Position of the first word of the phrase
	 * @return Context around the phrase
	 * 
	 */
	public List<String> getContext(String phrase, Document doc, int pos);
	

}
