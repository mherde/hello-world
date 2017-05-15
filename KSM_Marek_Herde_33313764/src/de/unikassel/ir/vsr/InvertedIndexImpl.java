package de.unikassel.ir.vsr;

import java.util.HashMap;
import java.util.Iterator;

public class InvertedIndexImpl implements InvertedIndex {

	/** data structure of the inverted index */
	private HashMap<String, TokenInfo> tokenHash;

	public InvertedIndexImpl(Corpus corpus) {
		for (Document currentDoc : corpus) {
			
			for (String currentToken : currentDoc) {
				
				TokenInfo tokenInfo = new TokenInfoImpl();
				
				tokenInfo.getTokenOccurrenceList().add(new TokenOccurrenceImpl(currentDoc, currentToken));
				
				tokenHash.put(currentToken, tokenInfo);
			}
		}
	}

	@Override
	public int getCorpusSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<? extends SearchResultItem> getCosineSimilarities(String[] query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenInfo getTokenInfo(String term) {
		// TODO Auto-generated method stub
		return null;
	}

}
