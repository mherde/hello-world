package de.unikassel.ir.vsr;

import java.util.ArrayList;
import java.util.List;

public class TokenOccurrenceImpl implements TokenOccurrence {

	/** reference to the document containing the token */
	private Document docRef;

	/** token info to corresponding token */
	private TokenInfo tokenInfo;

	/** term frequency in the document */
	private double documentTF;

	/** list of positions where the token occurs */
	private List<Integer> positions;

	/**
	 * flag, because positions counting is different in this class from the
	 * document class
	 */
	private boolean positionFlag = true;

	/**
	 * constructor to initialize all important values
	 * 
	 * @param docRef
	 * @param tokenInfo
	 * @param token
	 */
	public TokenOccurrenceImpl(Document docRef, TokenInfo tokenInfo, String token) {
		this.docRef = docRef;
		this.tokenInfo = tokenInfo;
		this.documentTF = ((DocumentImpl) this.docRef).getTF(token);

		/* counting positions ascending from 0 instead of from 1 */
		this.positions = new ArrayList<Integer>();
		this.positions.addAll(this.docRef.getTermPositions(token));

	}

	@Override
	public int compareTo(TokenOccurrence o) {

		if (this.equals(o)) {
			/*
			 * weights of both token occurrences are equal, becaue they are
			 * identical objects regarding equal method
			 */
			return 0;
		} else if (this.getWeight() > o.getWeight()) {
			/* this token occurrence has a greater weight */
			return -1;
		} else {
			/* this token occurrence has a smaller or equal weight */
			return 1;
		}
	}

	@Override
	public Document getDocument() {
		return this.docRef;
	}

	@Override
	public double getWeight() {
		/* calculation of tf-idf */
		return this.tokenInfo.getIdf() * this.documentTF;
	}

	@Override
	public List<Integer> getPositions() {
		if (this.positionFlag) {
			/* counting from 0 instead from 1 like in document class */
			for (int i = 0; i < this.positions.size(); i++) {
				int currentPosition = this.positions.remove(i);
				this.positions.add(i, currentPosition - 1);
			}
			this.positionFlag = false;
		}

		return this.positions;
	}

	@Override
	public String toString() {
		return String.format("%-15s -> %.15f", this.docRef.getId(), this.getWeight());
	}

}
