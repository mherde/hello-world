/*
 * Created on 01.11.2005
 */
package de.unikassel.ir.webapp;

import java.util.HashSet;
import java.util.Set;

/**
 * Beispiele für JDK-1.5-Neuerungen, die wir hier benutzen. 
 * 
 * @author Christoph Schmitz
 */
public class Jdk15Beispiel {
	
	/** 
	 * Methoden können variabel viele Parameter haben 
	 * <code>void foo(int... zahlen)</code> entspricht 
	 * <code>void foo(int[] zahlen)</code>
	 * mit syntaktischem Zucker beim Aufruf (s.u.)
	 */
	public static void foo(int... zahlen) {
		System.out.println("foo");
		
		// zur Verdeutlichung:
		int[] zahlen2 = zahlen;
		
		// iterieren mit neuer for-Schleife
		for (int zahl: zahlen2) {
			System.out.println(zahl);
		}
	}
	
	
	public static void main(String[] args) {
		// Generics: Kollektionen können mit bestimmten Datentypen versehen werden.
		Set<Integer> menge = new HashSet<Integer>();
		
		// ok
		menge.add(new Integer(1000));
		
		// auch ok: Konversion Integer<->int geht automatisch!
		menge.add(4711);
		
		// nicht ok: Typfehler zur Übersetzungszeit(!)
		// 
		// menge.add("Hallo!");
		
		// Iterieren über alles, was einen Iterator hat,
		// mit neumodischer for-Schleife
		for (int i: menge) {
			System.out.println(i);
		}
		
		// Aufruf mit variablen Parameterzahlen
		// ruft immer die gleiche Funktion auf!
		foo(1);
		foo(1,2,3);
		foo();
		foo(new int[] {4,5,6});
	}
}
