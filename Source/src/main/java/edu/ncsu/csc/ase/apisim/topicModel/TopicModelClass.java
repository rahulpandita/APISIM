package edu.ncsu.csc.ase.apisim.topicModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.EvalMode;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class TopicModelClass extends TopicModelFactory {
	
	public static final String OUTPUT_FILE_NAME_1 = "ClassSummaryPerTopic";
	
	public static final String OUTPUT_FILE_NAME_2 = "ClassSummaryPerClass";
	
	public static final String OUTPUT_FILE_NAME_3 = "ClassSummaryPerClassSimilarity";
	
	public TopicModelClass(EvalMode mode) throws Exception {
		super(mode);
	}
	
	@Override
	public InstanceList getInstanceList() {
		try {
			return InstanceCreator.createInstanceList(false, Configuration.MIDP_DUMP_PATH);
			//return InstanceCreator.createInstanceList(false, Configuration.ANDROID_DUMP_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public InstanceList getSInstanceList() {
		try {
			return InstanceCreator.createInstanceList(false, Configuration.ANDROID_DUMP_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try {
			return InstanceCreator.createInstanceList(false, Configuration.ANDROID_DUMP_PATH,
					Configuration.MIDP_DUMP_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try {
			return InstanceCreator.createInstanceListMIDP_Eval(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void runEval() throws Exception
	{
		Map<String ,List<String>> rankedMap = getRankedListPerTopic(MIN_PROB_DIST);
		writeToFile(rankedMap, OUTPUT_FILE_NAME_1, TOPK);
		writeToFile(getRankedListPerSearch(MIN_PROB_DIST), OUTPUT_FILE_NAME_2, TOPK);
		writeToFile(getSimilarListPerSearch(rankedMap, SIMILARITY_CUTOFF), OUTPUT_FILE_NAME_3, TOPK);
	}

	
	
	public Set<String> getSrcSet() {
		Set<String> returnSet = new TreeSet<String>();
		
		for(APIType type: AllClassCrawler.read(Configuration.MIDP_DUMP_PATH))
		{
			if (InstanceCreator.getEvalListMidp().contains(type.getName().trim()))
				returnSet.add(type.getPackage()+"."+type.getName());
		}
		
		
		return returnSet;
	}

	@Override
	public boolean inclusionCriteria(String str) {
		return str.trim().startsWith("ANDROID:");
	}

}
