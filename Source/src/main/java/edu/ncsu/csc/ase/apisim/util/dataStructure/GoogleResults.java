package edu.ncsu.csc.ase.apisim.util.dataStructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class GoogleResults 
{
	 public static void main(String[] args) throws Exception{
		 URL url = new URL(
	                "https://www.googleapis.com/customsearch/v1?key=YOUR-KEY&cx=013036536707430787589:_pqjad5hr1a&q=flowers&alt=json");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        BufferedReader br = new BufferedReader(new InputStreamReader(
	                (conn.getInputStream())));

	        String output;
	        System.out.println("Output from Server .... \n");
	        while ((output = br.readLine()) != null) {
	            System.out.println(output);
	        }

	        conn.disconnect();
		    
	 }
    
    public List<String> getGoogleResultUrlList(String searchTerm) throws Exception
    {
    	List<String> retURLList = new ArrayList<>();
    	String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	    String charset = "UTF-8";

	    URL url = new URL(google + URLEncoder.encode(searchTerm, charset));
	    Reader reader = new InputStreamReader(url.openStream(), charset);
	   
	    return retURLList;
    }
}
