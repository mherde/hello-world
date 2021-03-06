package de.unikassel.ir.vsr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLDocument extends DocumentImpl {

	/**
	 * global reference for the set containing all stop words, it is global
	 * because the stop word set shall be calculated only once and not for every
	 * new created HTMLDocument
	 */
	public static HashSet<String> stopwords = createStopWordSet();

	/**
	 * link to the HTMLDocument
	 */
	private URL url;

	/**
	 * links that are referenced by this HTMLDocument
	 */
	private Set<URL> links;

	/**
	 * datastructure for documents in jsoup
	 */
	private org.jsoup.nodes.Document parsedHTMLdoc;

	/**
	 * calculates a set of stop words that are already stemmed
	 * 
	 * @return set of stemmed stop words
	 */
	private static HashSet<String> createStopWordSet() {

		/* porter stemmer */
		Stemmer stemmer = new Stemmer();

		/* contains stemmed stop words */
		HashSet<String> stopWordsSet = new HashSet<String>();
		try {

			/* scanner to read file of stop words */
			Scanner sc = new Scanner(new File("resources/englishST.txt"), "UTF-8");
			while (sc.hasNext()) {

				/*
				 * parsing stop word in correct form; for the german stopwords
				 * the corresponding umlauts have to be added to the regex in
				 * replaceAll
				 */
				/*
				 * !! if numbers shall be included the expression in replaceAll,
				 * it would be replaceAll("[^A-Za-z0-9 -]","") !!
				 */
				String stopword = sc.next().toLowerCase().trim().replaceAll("[^A-Za-z -]", "");

				/* stemming stop word */
				stemmer.add(stopword.toCharArray(), stopword.length());
				stemmer.stem();
				String token = stemmer.toString();

				/* adding stemmed stop word to set */
				stopWordsSet.add(token);
			}

			/* closing scanner after work */
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return stopWordsSet;
	}

	/**
	 * constructor initialized with an URL
	 * 
	 * @param url
	 */
	public HTMLDocument(URL url) {
		this.url = url;
	}

	@Override
	public void read(InputStream input) throws IOException {

		/* represents the current position of scanned words */
		int currentPosition = 0;

		/* resetting size */
		this.size = 0;

		/*
		 * ensuring that with every read the set of url will be updated, if
		 * getExtractedLinks() is called
		 */
		this.links = null;

		/* parsing HTML document with JSoup parser */
		this.parsedHTMLdoc = Jsoup.parse(input, "UTF-8", this.url.toString());
		parsedHTMLdoc.setBaseUri(this.url.toString());

		/* removing java script and things not displayed on website */
		parsedHTMLdoc.select("script,.hidden,style,span").remove();

		/* initialization of the map containing all terms and its positions */
		this.termsIndex = new HashMap<>();

		/* all elements of the parsed HTML document */
		Elements elements = parsedHTMLdoc.getAllElements();
		/* text of the parsed HTML document */
		String text = parsedHTMLdoc.text();

		/*
		 * iteration over all elements in order to add text titles and alt tags
		 * to the whole text
		 */
		for (Element element : elements) {
			if (element.attr("title") != null) {
				text += " " + element.attr("title");
			}
			if (element.attr("alt") != null) {
				text += " " + element.attr("alt");
			}
		}

		/* porter stemmer */
		Stemmer stemmer = new Stemmer();

		/*
		 * iteration over all terms of the HTML document, many replacements are
		 * done before and also a split; for the german websites the
		 * corresponding umlauts have to be added to the regex in replaceAll
		 */
		/*
		 * !! if numbers shall be included the expression in replaceAll, it
		 * would be replaceAll("[^A-Za-z0-9 -]","") !!
		 */
		for (String term : text.toLowerCase().trim().replaceAll("[^A-Za-z -]", "").split("\\s+|-")) {

			/* stemming term to a token */
			stemmer.add(term.toCharArray(), term.length());
			stemmer.stem();
			String token = stemmer.toString();

			/* checking whether token is a stop word */
			if (!HTMLDocument.stopwords.contains(token) && token.length() > 0) {

				/*
				 * getting position list, if token already exists or creating a
				 * new one
				 */
				ArrayList<Integer> positionsOfToken = this.termsIndex.getOrDefault(token, new ArrayList<Integer>());

				/* adding current position to position list */
				positionsOfToken.add(currentPosition);

				/* adding token to data structure representing this document */
				this.termsIndex.put(token, positionsOfToken);

				/* increasing size and currentPosition by one */
				currentPosition++;
				this.size++;
			}
		}
	}

	/**
	 * determines all links of this HTML document
	 * 
	 * @return set of all links (URL) in this HTML document
	 */
	public Set<URL> getExtractedLinks() {

		/* if links have not been calculated yet */
		if (links == null) {
			/* stores links */
			links = new HashSet<>();
			System.out.println("Hello");

			/* all elements that contains links */
			Elements linkElements = this.parsedHTMLdoc.select("a[href]");

			/* iteration over all elements with links */
			for (Element linkElement : linkElements) {
				try {
					/* extracting link and adding it to set of links */
					String url = linkElement.attr("abs:href").toString().replaceAll(" ", "");
					if (url.length() > 0 && !url.equals(this.url.toString()))
						links.add(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		return this.links;
	}

}
