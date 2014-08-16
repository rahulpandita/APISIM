package edu.ncsu.csc.ase.apisim.topicModel;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class TopicModelClass extends TopicModelFactory {
	
	public static final String OUTPUT_FILE_NAME_1 = "ClassSummaryPerTopic";
	
	public static final String OUTPUT_FILE_NAME_2 = "ClassSummaryPerClass";
	
	public static void main(String[] args) {
		TopicModelFactory tm;
		try {
			tm = new TopicModelClass();
			tm.runEval();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public TopicModelClass() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void runEval() throws Exception
	{
		
		writeToFile(getRankedListPerTopic(0.001), OUTPUT_FILE_NAME_1, 30);
		writeToFile(getRankedListPerSearch(0.001), OUTPUT_FILE_NAME_2, 30);
	}

	@Override
	public InstanceList getInstanceList(){
		try 
		{
			return InstanceCreator.createInstanceList(Configuration.ANDROID_DUMP_PATH,Configuration.MIDP_DUMP_PATH,Configuration.CLDC_DUMP_PATH);
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
			return InstanceCreator.createInstanceList(Configuration.ANDROID_DUMP_PATH);
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
			return InstanceCreator.createInstanceList1(InstanceCreator.getEvalList(),Configuration.MIDP_DUMP_PATH,Configuration.CLDC_DUMP_PATH);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
