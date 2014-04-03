package edu.ncsu.csc.ase.apisim.spring.mvc.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.util.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;

public class APICache {
	private static APICache instance;

	private static Map<String, APIClass> AndroidMap;
	
	private static Map<String, APIClass> MidpMap;
	
	private static Map<String, APIClass> CldcMap;
	

	private APICache() {
		List<APIClass> clazzList = AllClassCrawler
				.read(Configuration.ANDROID_DUMP_PATH);
		AndroidMap = new LinkedHashMap<>();
		for(APIClass clazz: clazzList)
			AndroidMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		clazzList = AllClassCrawler
				.read(Configuration.MIDP_DUMP_PATH);
		MidpMap = new LinkedHashMap<>();
		for(APIClass clazz: clazzList)
			MidpMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		
		clazzList = AllClassCrawler
				.read(Configuration.CLDC_DUMP_PATH);
		CldcMap = new LinkedHashMap<>();
		for(APIClass clazz: clazzList)
			CldcMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		
	}

	public static synchronized APICache getInstance() {
		if (instance == null) {
			instance = new APICache();
		}
		return instance;
	}

	public List<APIClass> getClassList(APITYPE apiType) {
		List<APIClass> retList = new ArrayList<>();
		switch (apiType) {
		case ANDROID:
			retList.addAll(AndroidMap.values());
			break;
		case MIDP:
			retList.addAll(MidpMap.values());
			break;
		case CLDC:
			retList.addAll(CldcMap.values());
			break;
		default:
			break;
		}
		return retList;
	}

	public APIClass getAPIClass(APITYPE apiType, String choice) {
		APIClass retVal = null;
		switch (apiType) {
		case ANDROID:
			retVal =  AndroidMap.get(choice);
			break;
		case MIDP:
			retVal =  MidpMap.get(choice);
			break;
		case CLDC:
			retVal =  CldcMap.get(choice);
			break;
		default:
			break;
		}
		return retVal;
	}

}
