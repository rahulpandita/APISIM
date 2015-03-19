package edu.ncsu.csc.realsearch.apisim.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;


public class Cache {
	
	private static Cache instance;
	
	private static List<APIType> lst;
	
	private static Map<String, List<String>> pkgIdx;
	
	private static Map<String, APIType> classIdx;
	
	public static final String src_type = APITYPE.JAVA.name().toLowerCase();
	
	public static final String dest_type = APITYPE.DOTNET.name().toLowerCase();
	
	private Cache(){
	 populate();
	}
	
	private void populate() {
		switch (src_type) {
		case "midp":
			populateJ2ME();
			break;
		case "cldc":
			populateJ2ME();
			break;
			
		case "java":
			populateJava();
			break;

		default:
			break;
		}
		
	}
	
	private void populateJava() {
		lst = new ArrayList<APIType>();
		lst.addAll(AllClassCrawler.read(Configuration.JAVA_DUMP_PATH));
		
		pkgIdx = new HashMap<String, List<String>>();
		classIdx = new HashMap<String, APIType>();
		
		List<String> subClassList = new ArrayList<String>();
		
		subClassList.add("file");
		subClassList.add("reader");
		subClassList.add("writer");
		subClassList.add("hashmap");
		subClassList.add("arraylist");
		subClassList.add("iterator");
		subClassList.add("calendar");
		subClassList.add("resultset");
		subClassList.add("statement");
		subClassList.add("connection");
		
		for(APIType typ:lst)
		{
			if(typ.getPackage().trim().equals("java.util"))
			{
				if(subClassList.contains(typ.getName().trim().toLowerCase().trim()))
				{
					if(!pkgIdx.containsKey(typ.getPackage()))
					{
						pkgIdx.put(typ.getPackage(), new ArrayList<String>());
					}
					pkgIdx.get(typ.getPackage()).add(typ.getName());
					classIdx.put(typ.getPackage()+"."+typ.getName(), typ);
				}
			}
		}

	}
	
	private void populateJ2ME() {
		lst = new ArrayList<APIType>();
		lst.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		lst.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		List<String> subClassList = new ArrayList<String>();
		
		subClassList.add("Alert");
		subClassList.add("Command");
		subClassList.add("Font");
		//subClassList.add("Displayable");
		//subClassList.add("Display");
		subClassList.add("Canvas");
		//subClassList.add("GameCanvas");
		//subClassList.add("Layer");
		//subClassList.add("Sprite");
		subClassList.add("Graphics");
		//subClassList.add("Image");
		
		pkgIdx = new HashMap<String, List<String>>();
		classIdx = new HashMap<String, APIType>();
		for(APIType typ:lst)
		{
			if(subClassList.contains(typ.getName().trim()))
			{
				if(!pkgIdx.containsKey(typ.getPackage()))
				{
					pkgIdx.put(typ.getPackage(), new ArrayList<String>());
				}
				pkgIdx.get(typ.getPackage()).add(typ.getName());
				classIdx.put(typ.getPackage()+"."+typ.getName(), typ);
			}
		}

	}
	
	public void repopulate(){
		populate();
		System.err.println("Flushed Cache!!");
	}

	public static synchronized Cache getInstance()
	{
		if(instance==null)
			instance = new Cache();
		return instance;
	}
	
	public APIType getClass(String name)
	{
		return classIdx.get(name);
	}
	
	public Map<String, List<String>> getPkgIdx() {
		return pkgIdx;
	}

	public Map<String, APIType> getClassIdx() {
		return classIdx;
	}

	public Map<String, String> getClassMtdIdx(String name) {
		Map<String, String> mtdList = new LinkedHashMap<String, String>();
		APIType typ = getClass(name);
		for(APIMtd mtd: typ.getConstructors())
		{
			mtdList.put(mtd.toString(), mtd.getName());
		}
		for(APIMtd mtd: typ.getMethod())
		{
			mtdList.put(mtd.toString(), mtd.getName());
		}
		return mtdList;
	}
	
	public APIMtd getClassMtdAtIdx(String name, Integer idx) {
		APIType typ = getClass(name);
		int consLstSize = typ.getConstructors().size();
		if(idx<consLstSize)
			return typ.getConstructors().get(idx);
		else
			return typ.getMethod().get(idx-consLstSize);
	}
	
}
