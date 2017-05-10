package de.unikassel.ir.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unikassel.ir.vsr.Corpus;
import de.unikassel.ir.vsr.CorpusImpl;
//import de.unikassel.ir.vsr.CorpusImpl;
import de.unikassel.ir.vsr.Document;
//import de.unikassel.ir.vsr.DocumentImpl;
import de.unikassel.ir.vsr.DocumentImpl;

/**
 * Search Engine Bean to use the query methods in JSPs
 * @author Beate Krause
 */
public class SearchEngine {
	
	/**
	 * The corpus
	 */
	private Corpus corpus;

	/**
	 * Constructor, sets the corpus
	 */
	public SearchEngine() {
		corpus = new CorpusImpl();
		loadDefaultCorpus();
	}
	
	/**
	 * Load Texts into the corpus
	 */
	public void loadDefaultCorpus() {
		
		//TODO: set path for corpus
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
	 * Query for terms in the corpus
	 * @param terms query
	 * @param andOperator AND or OR query
	 * @return set of document ids
	 */
	public Set<String> testQuery(String terms, boolean andOperator) {
		
		String[] termsRequest = terms.split("\\s+");
		
		
		Set<String> resultIDs = new HashSet<String>();
		Collection<Document> docs;
		
		if (andOperator){
			docs = corpus.getDocumentsContainingAll(termsRequest);
		}else{
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