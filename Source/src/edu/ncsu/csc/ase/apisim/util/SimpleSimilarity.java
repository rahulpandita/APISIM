package edu.ncsu.csc.ase.apisim.util;

import java.util.ArrayList;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIMethod;

public class SimpleSimilarity 
{
	private static ArrayList<APIClass> clazzListMIDP;
	
	private static ArrayList<APIClass> clazzListCLDC;
	
	private static ArrayList<APIClass> clazzListAndroid;
	
	private static ArrayList<APIClass> clazzListMIDPRed;
	
	private static ArrayList<APIClass> clazzListCLDCRed;
	
	private static ArrayList<APIClass> clazzListAndroidRed;
	
	
	
	public SimpleSimilarity() 
	{
		
		ArrayList<APIClass> clazzListMIDPComm = new ArrayList<>();
		
		ArrayList<APIClass> clazzListAndroidComm = new ArrayList<>();
		
		clazzListAndroidRed = new ArrayList<>();
		
		clazzListMIDPRed = new ArrayList<>();
		
		clazzListMIDP = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
        
		
		clazzListAndroid = AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH);
        
		
		for(APIClass clazzAndroid : clazzListAndroid) 
		{
            for(APIClass clazzMIDP: clazzListMIDP)
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
		
		for(APIClass clazzAndroid : clazzListAndroid) 
		{
			if(!clazzListAndroidComm.contains(clazzAndroid))
			{
				clazzListAndroidRed.add(clazzAndroid);
			}
		}
		
		for(APIClass clazzMIDP : clazzListMIDP) 
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

	public ArrayList<APIClass> getClazzListMIDPRed() {
		return clazzListMIDPRed;
	}



	public ArrayList<APIClass> getClazzListAndroidRed() {
		return clazzListAndroidRed;
	}
	
	
}
