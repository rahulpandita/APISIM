package edu.ncsu.csc.ase.apisim.spring.mvc.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class APICache {
	private static APICache instance;

	private static Map<String, APIType> AndroidMap;
	
	private static Map<String, APIType> MidpMap;
	
	private static Map<String, APIType> CldcMap;
	

	private APICache() {
		List<APIType> clazzList = AllClassCrawler
				.read(Configuration.ANDROID_DUMP_PATH);
		AndroidMap = new LinkedHashMap<>();
		for(APIType clazz: clazzList)
			AndroidMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		clazzList = AllClassCrawler
				.read(Configuration.MIDP_DUMP_PATH);
		MidpMap = new LinkedHashMap<>();
		for(APIType clazz: clazzList)
			MidpMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		
		clazzList = AllClassCrawler
				.read(Configuration.CLDC_DUMP_PATH);
		CldcMap = new LinkedHashMap<>();
		for(APIType clazz: clazzList)
			CldcMap.put(clazz.getPackage() + "." + clazz.getName(), clazz);
		
		
	}

	public static synchronized APICache getInstance() {
		if (instance == null) {
			instance = new APICache();
		}
		return instance;
	}

	public List<APIType> getClassList(APITYPE apiType) {
		List<APIType> retList = new ArrayList<>();
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

	public APIType getAPIClass(APITYPE apiType, String choice) {
		APIType retVal = null;
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
