package edu.ncsu.csc.ase.apisim.topicModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.EvalMode;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class TopicModelPkgClassMtdJavaNET extends TopicModelFactory {

	public String OUTPUT_FILE_NAME_1 = "JavaNETPkgClassMtdSummaryPerTopic";

	public String OUTPUT_FILE_NAME_2 = "JavaNETPkgClassMtdSummaryPerPkg";
	
	public String OUTPUT_FILE_NAME_3 = "JavaNETPkgClassMtdSummaryPerPkgSimilarity";

	public TopicModelPkgClassMtdJavaNET (EvalMode mode) throws Exception {
		super(mode);
	}
	
	public TopicModelPkgClassMtdJavaNET (int numTopics, EvalMode mode) throws Exception {
		super(numTopics, mode);
		OUTPUT_FILE_NAME_1 = OUTPUT_FILE_NAME_1 + numTopics;
		OUTPUT_FILE_NAME_2 = OUTPUT_FILE_NAME_2 + numTopics;
		OUTPUT_FILE_NAME_3 = OUTPUT_FILE_NAME_3 + numTopics;
	}
	
	@Override
	public InstanceList getInstanceList() {
		try {
			return InstanceCreator.createInstanceListJavaPkgClass(true);
			//return InstanceCreator.createInstanceListandroidpkgClass(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public InstanceList getSInstanceList() {
		try {
			return InstanceCreator.createInstanceListNetPkgClass(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try {
			return InstanceCreator.createInstanceListJavaNetPkgCls(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try {
			return InstanceCreator.createInstanceListJavaPkgClass(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void runEval() throws Exception {
		Map<String ,List<String>> rankedMap = getRankedListPerTopic(MIN_PROB_DIST);
		//writeToFile(rankedMap, OUTPUT_FILE_NAME_1, TOPK);
		writeToFile(getRankedListPerSearch(MIN_PROB_DIST), OUTPUT_FILE_NAME_2, TOPK);
		writeToFile(getSimilarListPerSearch(rankedMap, SIMILARITY_CUTOFF), OUTPUT_FILE_NAME_3, TOPK);
		
	}
	
	@Override
	public Set<String> getSrcSet() {
		Set<String> srcClassSet = new HashSet<String>();
		List<APIType> typeList = AllClassCrawler.read(Configuration.JAVA_DUMP_PATH);
		String pkgStr;
		for(APIType type: typeList)
		{
			pkgStr = type.getPackage();
			if(!srcClassSet.contains(pkgStr))
				srcClassSet.add(pkgStr);
		}
		return srcClassSet;
	}
	@Override
	public boolean inclusionCriteria(String str) {
		return InstanceCreator.getEvalListJava().contains(str);
	}
}