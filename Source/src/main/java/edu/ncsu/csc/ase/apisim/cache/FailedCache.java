package edu.ncsu.csc.ase.apisim.cache;

import java.util.HashMap;
import java.util.Map;

public class FailedCache 
{
	private static FailedCache instance;
	
	private static Map<String, String> failedUrlMap;
	
	public static synchronized FailedCache getInstance()
	{
		if(instance == null)
			instance = new FailedCache();
		return instance;
	}
	
	private FailedCache()
	{
		failedUrlMap = new HashMap<>();
	}
	
	public void addFailedURL(String name, String url)
	{
		failedUrlMap.put(url, name);
	}
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Failed to get\n");
		for(String url: failedUrlMap.keySet())
		{
			buff.append("\t");
			buff.append(failedUrlMap.get(url));
			buff.append(" : ");
			buff.append(url);
			buff.append("\n");
		}
		return buff.toString();
	}

}
