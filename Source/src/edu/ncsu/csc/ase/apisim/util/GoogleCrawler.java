package edu.ncsu.csc.ase.apisim.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import com.google.gson.Gson;

import edu.ncsu.csc.ase.apisim.util.dataStructure.GoogleResults;

public class GoogleCrawler 
{
	
	private static final String GOOGLE_SEARCH_URL_PREFIX = "http://www.google.com/search?q=";
	private static final String GOOGLE_SEARCH_USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
	
	
	public static void main1(String[] args) {
		GoogleCrawler gc = new GoogleCrawler();
		String searchTerm = "Draws the outline of the specified rectangle using the current color and stroke style.";
		List<String> googleQueryList = gc.createGoogleQueryUrl(searchTerm);
		for(String googleQuery: googleQueryList)
		{
			gc.performGoogleSearch(googleQuery);
		}
	}
	
	
	
	private List<String>  createGoogleQueryUrl(String searchTerm)
	{
		List<String> retList = new ArrayList<>();
		StringBuffer buff = new StringBuffer();
		buff.append(GOOGLE_SEARCH_URL_PREFIX);
		buff.append(searchTerm.replaceAll("\\s", "+"));
		buff.append("+android");
		
		retList.add(buff.toString());
		System.err.println(buff.toString());
		return retList;
	}
	
	private List<String> performGoogleSearch(String url)
	{
		List<String> retURLList = new ArrayList<>();
		try 
		{
			Document googleSearchPage = Jsoup.connect(url).userAgent(GOOGLE_SEARCH_USER_AGENT).get();
			Elements links = googleSearchPage.select("h3.r");
			for (Element link : links)
			{
				Elements urlLink = link.getElementsByTag("a");
				for (Element lnk : urlLink) 
				{
				  System.err.println(lnk.text() +"-->"+ lnk.attr("href"));
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		return retURLList;
	}
	
}
