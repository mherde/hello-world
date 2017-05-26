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

	private HashSet<String> stopwords = new HashSet<String>();

	private URL url;

	private Set<URL> links;

	private org.jsoup.nodes.Document doc;

	public HTMLDocument(URL url) {
		this.url = url;
		try {
			Scanner sc = new Scanner(new File("resources/englishST.txt"), "UTF-8");
			while (sc.hasNext()) {
				stopwords.add(sc.next());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void read(InputStream input) throws IOException {

		/* represents the current position of scanned words */
		int currentPosition = 1;

		this.doc = Jsoup.parse(input, "UTF-8", this.url.toString());
		doc.setBaseUri(this.url.toString());

		Stemmer stemmer = new Stemmer();
		/* initialization of the map */
		this.termsIndex = new HashMap<>();

		Elements images = doc.getElementsByTag("img");

		for (Element img : images) {
			
		}
		
		Elements elements = doc.getAllElements();
		String text = doc.text();
		

		for (Element element : elements) {
			if (element.attr("title") != null) {
				text += " "+element.attr("title");
			}
			if (element.attr("alt") != null) {
				text += element.attr("alt");
			}
		}
		
		
		

		for (String term : text.toLowerCase().trim().replaceAll("[^A-Za-z0-9 -]", "").split("\\s+|-")) {
			if (!this.stopwords.contains(term)) {
				stemmer.add(term.toCharArray(), term.length());
				stemmer.stem();
				String token = stemmer.toString();
				System.out.println(token);
				ArrayList<Integer> positionsOfToken = this.termsIndex.getOrDefault(token, new ArrayList<Integer>());
				positionsOfToken.add(currentPosition);
				this.termsIndex.put(token, positionsOfToken);
				currentPosition++;
				this.size++;
			}
		}
		

	}

	public Set<URL> getExtractedLinks() {
		if (links == null) {
			links = new HashSet<>();
			Elements linkElements = this.doc.select("a[href]");

			for (Element linkElement : linkElements) {
				try {
					links.add(new URL(linkElement.attr("href").toString()));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return this.links;
	}

}
