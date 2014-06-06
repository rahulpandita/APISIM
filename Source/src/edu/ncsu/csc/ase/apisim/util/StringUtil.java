package edu.ncsu.csc.ase.apisim.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

/**
 * Class containing common String utility routines
 * 
 * @author rahul_pandita
 * 
 */
public class StringUtil {
	
	/**
	 * Utility function to remove spaces
	 * 
	 * @param str
	 *            the String in which spaces are to be removed
	 * @return String with all spaces stripped
	 */
	public static String removeSpaces(String str) {
		return str.replaceAll("\\s", "");
	}

	/**
	 * Utility function to split Camel Case notation Code Adapted from <a href=
	 * "http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java"
	 * >StackOverflow</a>
	 * 
	 * @param s
	 * @return
	 */
	public static String splitCamelCase(String s) {
		return s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}
	
	/**
	 * Cleans the HTML Content of a String
	 * Also replaces {@code CharSequence} elements returned 
	 * by {@link #cleanCharactersList()} with a space
	 * @param htmlText
	 * @return cleaned input String
	 */
	public static String cleanHTML(String htmlText) {
		htmlText = Jsoup.parse(htmlText).text();
		for(CharSequence seq: cleanCharactersList())
		{
			while(!htmlText.equals(htmlText.replace(seq, " ")))
				htmlText = htmlText.replace(seq, " ");
		}
		htmlText = htmlText.replaceAll("\\s+", " ");
		return htmlText.trim();
	}
	
	/**
	 * Returns a {@link List} containing {@code CharSequence} commonly used to
	 * clean text. 
	 * @return
	 */
	public static List<CharSequence> cleanCharactersList()
	{
		List<CharSequence> cleanList = new ArrayList<>();
		cleanList.add("\n");
		cleanList.add("\t");
		cleanList.add("\u00a0");
		return cleanList;
	}
	
}
