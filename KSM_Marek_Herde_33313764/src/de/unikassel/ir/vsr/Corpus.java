/*
 * Created on 01.11.2005
 */
package de.unikassel.ir.vsr;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a collection of documents.
 * 
 * @author Christoph Schmitz
 * @author Stephan Doerfel
 */
public interface Corpus extends Iterable<Document> {
	
	/**
	 * Insert a document into the collection
	 * 
	 * @param doc the document to insert
	 */
	public void addDocument(Document doc);
	
	/**
	 * Iterator over all documents in this corpus
	 * 
	 * @return Iterator
	 */
	public Iterator<Document> iterator();
	
	/**
	 * Return all documents that contain each of the terms at least once
	 * 
	 * @param terms one or more terms
	 * @return Collection, containing all the documents for the terms
	 */
	public Collection<Document> getDocumentsContainingAll(String... terms);
	
	/**
	 * Return all documents that contain at least one of the terms at least once
	 * 
	 * @param terms one or more terms
	 * @return Collection, containing all the documents for the terms
	 */
	public Collection<Document> getDocumentsContainingAny(String... terms);

	/**
	 * Return the number of documents in the corpus
	 * 
	 * @return number of documents in the corpus
	 */
	public int size();
}
