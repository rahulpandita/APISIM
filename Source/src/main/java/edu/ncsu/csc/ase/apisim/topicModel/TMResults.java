package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.ase.apisim.util.FileUtil;

public class TMResults 
{
	public static void main(String[] args) throws Exception {
		List<String> tstList = new ArrayList<String>();
		for(int i=0; i<100;i++)
		tstList.add(""+i);
		System.out.println(tstList.subList(0, 10));
				
	}
	
	public static final String clsSum = "tpmdling"+ File.separator +"ClassSummaryPerClassSimilarity.txt";
	public static final String clsMtdSum = "tpmdling"+ File.separator +"ClassMtdSummaryPerClassSimilarity.txt";
	public static final String pkgSum = "tpmdling"+ File.separator +"PkgSummaryPerPkgSimilarity.txt";
	public static final String pkgClsSum = "tpmdling"+ File.separator +"PkgClassSummaryPerPkgSimilarity.txt";
	public static final String pkgClsMtdSum = "tpmdling"+ File.separator + "PkgClassMtdSummaryPerPkgSimilarity.txt";
	
	
	public static Map<String, List<String>> read(String fileName) throws Exception
	{
		List<String> lines = FileUtil.readLines(new File(fileName));
		Map<String, List<String>> returnMap = new LinkedHashMap<String, List<String>>();
		String key ="";
		for(String line: lines)
		{
			if(!line.startsWith("\t"))
			{
				if(line.startsWith("_________________________________________________________________________________"))
					continue;
				else
				{
					key = line.trim();
					returnMap.put(key, new ArrayList<String>());
				}
			}
			else
			{
				line = line.trim();
				line = line.replace("ANDROID:", "");
				line = line.split("\t")[0];
				returnMap.get(key).add(line.trim());
			}
		}
		return returnMap;
	}
	
	
	public static Map<String, List<String>> readPkg(String fileName)throws Exception
	{
		//PkgClassSummaryPerPkgSimilarity.txt
		List<String> lines = FileUtil.readLines(new File(fileName));
		Map<String, List<String>> returnMap = new LinkedHashMap<String, List<String>>();
		String key ="";
		for(String line: lines)
		{
			if(!line.startsWith("\t"))
			{
				if(line.startsWith("_________________________________________________________________________________"))
					continue;
				else
				{
					key = line.trim();
					returnMap.put(key, new ArrayList<String>());
				}
			}
			else
			{
				line = line.trim();
				line = line.split("\t")[0];
				returnMap.get(key).add(line.trim());
			}
		}
		return returnMap;
	}
	public static void writeToFile(Map<String, List<String>> finalMap) 
	{
		String fileName = "tpmdling"+ File.separator +"ClassMtdSummaryPerClassSimilarity1.txt";
		try 
		{
			PrintWriter out = new PrintWriter(new File(fileName));
			List<String> lst;
			for(Object key: finalMap.keySet())
			{
				out.println(key.toString());
				lst = finalMap.get(key);
				for (int i = 0; i < lst.size(); i++) {
					out.println("\t" + lst.get(i) );
				}
				out.println("_________________________________________________________________________________");
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
	}
}
