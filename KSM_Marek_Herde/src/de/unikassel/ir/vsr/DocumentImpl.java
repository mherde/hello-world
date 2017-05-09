package de.unikassel.ir.vsr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DocumentImpl implements Document {

	/**
	 * represents id of this document
	 */
	private String id;
	/**
	 * mapping between term and an list of position where this term occurs
	 */
	private HashMap<String, ArrayList<Integer>> termsIndex;
	/**
	 * represents size or rather number of words in this document
	 */
	private int size = 0;

	/* constructor with initializing of document id */
	public DocumentImpl(String fileName) {
		this.id = fileName;
	}

	@Override
	public Iterator<String> iterator() {

		/* simply return an iterator on the keys of the map */
		return this.termsIndex.keySet().iterator();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void read(InputStream input) throws IOException {

		/* represents the current position of scanned words */
		int currentPosition = 1;

		/* reader to read document */
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		
		/* stores all lines of the document */
		ArrayList<String[]> lines = new ArrayList<>();
		
		/* stores currentLine */
		String currentLine;
		
		/* reading all lines of document */
		while ((currentLine = in.readLine()) != null) {
			
			/* adding line as array of terms */
			lines.add(currentLine.trim().split("\\s+"));
		}

		/* initialization of the map */
		this.termsIndex = new HashMap<>();

		/* iteration over all lines of the document */
		for (String[] termsOfOneLine : lines) {

			/* iteration over all terms of the current lime */
			for (String term : termsOfOneLine) {

				/* if word is not listed in map */
				if (!this.termsIndex.containsKey(term)) {

					/* create an entry with a corresponding arraylist */
					ArrayList<Integer> positions = new ArrayList<>();

					/* adding position where the current word occurs */
					positions.add(currentPosition);

					this.termsIndex.put(term, positions);
				} else {

					/* getting corresponding position list */
					ArrayList<Integer> positions = this.termsIndex.get(term);

					/* adding position where the current word occurs */
					positions.add(currentPosition);
				}

				/* increasing current position */
				currentPosition++;
				/* increasing number of scanned words */
				this.size++;
			}
		}

	}

	@Override
	public int getTermCount(String term) {

		/* if this document contains the term */
		if (this.termsIndex.containsKey(term)) {

			/*
			 * calculation of number of occurences by getting number of
			 * positions
			 */
			return this.termsIndex.get(term).size();

			/* if this document does not contain the term */
		} else {

			/* number of occurrences is 0 */
			return 0;
		}
	}

	@Override
	public ArrayList<Integer> getTermPositions(String term) {

		/*
		 * returning list of positions by searching for the corresponding map
		 * entry
		 */
		return this.termsIndex.get(term);
	}

	@Override
	public int size() {

		/* return number of words in this document */
		return this.size;
	}

}