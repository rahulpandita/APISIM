package edu.ncsu.csc.ase.apisim.topicModel;

import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class TopicModelClassMtd extends TopicModelFactory {
	
	public static final String OUTPUT_FILE_NAME_1 = "ClassMtdSummaryPerTopic";
	
	public static final String OUTPUT_FILE_NAME_2 = "ClassMtdSummaryPerClass";
	
	public static final String OUTPUT_FILE_NAME_3 = "ClassMtdSummaryPerClassSimilarity1";
	
	public static void main(String[] args) {
		TopicModelFactory tm;
		try {
			tm = new TopicModelClassMtd();
			tm.runEval();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public TopicModelClassMtd() throws Exception {
		super(2500, 2, 10000);
		
	}

	@Override
	public void runEval() throws Exception
	{
		
		writeToFile(getRankedListPerTopic(0.001), OUTPUT_FILE_NAME_1, 30);
		writeToFile(getRankedListPerSearch(0.001), OUTPUT_FILE_NAME_2, 30);
		writeToFile(getSimilarListPerSearch(getRankedListPerTopic(0), 10), OUTPUT_FILE_NAME_3, 100);
	}

	@Override
	public InstanceList getInstanceList(){
		try 
		{
			return InstanceCreator.createInstanceList(true, Configuration.ANDROID_DUMP_PATH);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try 
		{
			return InstanceCreator.createInstanceList(true, Configuration.ANDROID_DUMP_PATH,Configuration.MIDP_DUMP_PATH);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try 
		{
			return InstanceCreator.createInstanceList22(true, Configuration.MIDP_DUMP_PATH);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Set<String> getSrcSet() {
		Set<String> returnSet = new TreeSet<String>();
		
		for(APIType type: AllClassCrawler.read(Configuration.MIDP_DUMP_PATH))
		{
			if (InstanceCreator.getEvalList().contains(type.getName().trim()))
				returnSet.add(type.getPackage()+"."+type.getName());
		}
		
		
		return returnSet;
	}
	
	@Override
	public boolean inclusionCriteria(String str) {
		return str.trim().startsWith("ANDROID:");
	}

}
