package edu.ncsu.csc.ase.apisim.util;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class SimpleSimilarity 
{
	private static List<APIType> clazzListMIDP;
	
	private static List<APIType> clazzListCLDC;
	
	private static List<APIType> clazzListAndroid;
	
	private static List<APIType> clazzListMIDPRed;
	
	private static List<APIType> clazzListCLDCRed;
	
	private static List<APIType> clazzListAndroidRed;
	
	
	
	public SimpleSimilarity() 
	{
		
		ArrayList<APIType> clazzListMIDPComm = new ArrayList<>();
		
		ArrayList<APIType> clazzListAndroidComm = new ArrayList<>();
		
		clazzListAndroidRed = new ArrayList<>();
		
		clazzListMIDPRed = new ArrayList<>();
		
		clazzListMIDP = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
        
		
		clazzListAndroid = AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH);
        
		
		for(APIType clazzAndroid : clazzListAndroid) 
		{
            for(APIType clazzMIDP: clazzListMIDP)
            {
				if(clazzAndroid.getPackage().trim().equals(clazzMIDP.getPackage().trim()))
            	{
					if(clazzAndroid.getName().trim().equals(clazzMIDP.getName().trim()))
            		{	
						clazzListAndroidComm.add(clazzAndroid);
						clazzListMIDPComm.add(clazzMIDP);
            		}
            	}
            	
            }
        }
		
		for(APIType clazzAndroid : clazzListAndroid) 
		{
			if(!clazzListAndroidComm.contains(clazzAndroid))
			{
				clazzListAndroidRed.add(clazzAndroid);
			}
		}
		
		for(APIType clazzMIDP : clazzListMIDP) 
		{
			if(!clazzListMIDPComm.contains(clazzMIDP))
			{
				clazzListMIDPRed.add(clazzMIDP);
			}
		}
		AllClassCrawler.write(clazzListMIDPRed, "midpRed.api");
	}

	public static void main(String[] args) {
		new SimpleSimilarity();
	}

	public List<APIType> getClazzListMIDPRed() {
		return clazzListMIDPRed;
	}



	public List<APIType> getClazzListAndroidRed() {
		return clazzListAndroidRed;
	}
	
	
}
