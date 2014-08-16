package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class Test {
	public static void main(String[] args) throws Exception {
		createInstanceList(Configuration.ANDROID_DUMP_PATH,Configuration.CLDC_DUMP_PATH, Configuration.MIDP_DUMP_PATH);
		//createInstanceList(Configuration.CLDC_DUMP_PATH, Configuration.MIDP_DUMP_PATH);
	}
	
	public static List<String> createInstanceList(String...apiDumpPath) throws Exception {
		List<String> insList = new ArrayList<String>();
		Map<String, List<String>> map = new TreeMap<String, List<String>>();
		String aa;
		for(String API:apiDumpPath)
		{
			for(APIType type: AllClassCrawler.read(API))
			{
				aa = type.getPackage()+ "\t" + type.getName();
				if(!map.containsKey(aa))
				{
					map.put(aa, new ArrayList<String>());
				}
				map.get(aa).add(type.getApiName());
			}
		}
		
		
		try 
		{
			PrintWriter out = new PrintWriter(new File("abcc1.txt"));
			for(String key: map.keySet())
			{
				if(map.get(key).size()==1)
					out.println(map.get(key).get(0)+"\t"+ key);
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return insList;
	}
		
}
