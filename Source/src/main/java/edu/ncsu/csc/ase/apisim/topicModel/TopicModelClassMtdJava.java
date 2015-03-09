package edu.ncsu.csc.ase.apisim.topicModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class TopicModelClassMtdJava extends TopicModelFactory {
	
	public static final String OUTPUT_FILE_NAME_1 = "ClassMtdSummaryPerTopicJava";
	
	public static final String OUTPUT_FILE_NAME_2 = "ClassMtdSummaryPerClassJava";
	
	public static final String OUTPUT_FILE_NAME_3 = "ClassMtdSummaryPerClassSimilarityJava";
	
	public TopicModelClassMtdJava() throws Exception {
		super(1000, 10, 20000);
	}
	
	public TopicModelClassMtdJava(int topics, int numThreads, int numIterations) throws Exception {
		super(topics, numThreads, numIterations);
	}
	
	@Override
	public void runEval() throws Exception
	{
		Map<String ,List<String>> rankedMap = getRankedListPerTopic(MIN_PROB_DIST);
		//writeToFile(rankedMap, OUTPUT_FILE_NAME_1, TOPK);
		//writeToFile(getRankedListPerSearch(MIN_PROB_DIST), OUTPUT_FILE_NAME_2, TOPK);
		writeToFile(getSimilarListPerSearch(rankedMap, 50), OUTPUT_FILE_NAME_3, TOPK);
	}

	@Override
	public InstanceList getInstanceList(){
		try 
		{
			return InstanceCreator.createInstanceList(true, Configuration.DOTNET_DUMP_PATH);
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
			return InstanceCreator.createInstanceList(true, Configuration.DOTNET_DUMP_PATH,Configuration.JAVA_DUMP_PATH);
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
			return InstanceCreator.createSearchInstanceListJava(true, Configuration.JAVA_DUMP_PATH);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Set<String> getSrcSet() {
		Set<String> returnSet = new TreeSet<String>();
		
		for(APIType type: AllClassCrawler.read(Configuration.JAVA_DUMP_PATH))
		{
			if (InstanceCreator.getEvalList1().contains(type.getPackage().trim()))
				returnSet.add(type.getPackage()+"."+type.getName());
		}
		
		
		return returnSet;
	}
	
	@Override
	public boolean inclusionCriteria(String str) {
		return str.trim().startsWith("DOTNET:");
	}
	
	public static void main(String[] args) {
		try{
			TopicModelClassMtdJava tm = new TopicModelClassMtdJava();
			tm.eval();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
