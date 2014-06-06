package edu.ncsu.csc.ase.apisim.dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.dristi.util.ConsoleUtil;

public class APITypeFactory 
{
	private static APITypeFactory instance = null;
	
	private static Map<String, APIType> typeMap = null;
	
	private static Map<String, List<String>> typeMapSeperateidx = null;
	
	public static synchronized APITypeFactory getInstance()
	{
		if(instance == null)
			instance = new APITypeFactory();
		return instance;
	}
	
	private APITypeFactory() 
	{
		typeMap = new HashMap<String, APIType>();
		typeMapSeperateidx = new HashMap<String, List<String>>();
		
		List<APIType> list = AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH);
		for(APIType clazz: list)
		{
			addNewType(clazz.getPackage(), clazz.getName());
		}
	}
	
	public synchronized APIType getAPIType(String typeName)
	{
		if(!existsAPIType(typeName))
		{
			if(!existsAPITypeSecondary(typeName))
			{
				System.out.println("TypeDef Not Found! Manully Creating one for :" + typeName);
				manuallyCreateAPIType();
			}
			else
			{
				List<String> typeList = typeMapSeperateidx.get(typeName);
				System.out.println("Multiple Types with same Name. Please select one");
				for(int i=0; i<typeList.size();i++)
				{
					System.out.println(i +")" + typeList.get(i));
				}
				int x = ConsoleUtil.readConsole("Enter the choice ...", 0, typeList.size()-1);
				typeName = typeList.get(x);
				//TODO Error Handling
			}
		}
		return typeMap.get(typeName);
	}
	
	private void addNewType(String pkgName, String typeName) {
		typeMap.put(pkgName + "." + typeName, new APIType(pkgName, typeName));
		if (!typeMapSeperateidx.containsKey(typeName))
			typeMapSeperateidx.put(typeName, new ArrayList<String>());
		List<String> typeList = typeMapSeperateidx.get(typeName);
		typeList.add(pkgName + "." + typeName);
		typeMapSeperateidx.put(typeName, typeList);
	}
	
	
	public synchronized APIType getAPIType(String pkgName, String typeName)
	{
		if(!existsAPIType(pkgName + "." + typeName))
		{
			System.out.println("TypeDef Not Found! Manully Creating one!");
			addNewType(pkgName, typeName);
		}
		return typeMap.get(pkgName + "." + typeName);
	}

	private void manuallyCreateAPIType() {
		String pkg = ConsoleUtil.readConsole("Enter Package Name");
		String typ = ConsoleUtil.readConsole("Enter Type Name");
		addNewType(pkg, typ);
	}
	
	private boolean existsAPIType(String typeName)
	{
		return typeMap.containsKey(typeName);
	}
	
	private boolean existsAPITypeSecondary(String typeName)
	{
		return typeMapSeperateidx.containsKey(typeName);
	}
}
