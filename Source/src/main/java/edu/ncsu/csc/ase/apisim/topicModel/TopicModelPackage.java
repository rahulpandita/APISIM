package edu.ncsu.csc.ase.apisim.topicModel;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.PackageSummaryCrawler;

public class TopicModelPackage extends TopicModelFactory {

	public static final String OUTPUT_FILE_NAME_1 = "PkgSummaryPerTopic";

	public static final String OUTPUT_FILE_NAME_2 = "PkgSummaryPerPkg";

	public static final String OUTPUT_FILE_NAME_3 = "PkgSummaryPerPkgSimilarity";
	
	public TopicModelPackage() throws Exception {
		super(500, 5, 20000);
		setup();

	}

	public TopicModelPackage(int topics, int numThreads, int numIterations) throws Exception {
		super(topics, numThreads, numIterations);
		setup();
	}

	private void setup() {
		MIN_PROB_DIST = 0.01;
		TOPK = 200;
		SIMILARITY_CUTOFF = 10;
	}
	
	@Override
	public InstanceList getInstanceList() {

		try {
			return InstanceCreator.createInstanceListAndroid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try {
			return InstanceCreator.createInstanceListPkg();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try {
			return InstanceCreator.createInstanceListmidp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void runEval() throws Exception {
		Map<String ,List<String>> rankedMap = getRankedListPerTopic(MIN_PROB_DIST);
		writeToFile(rankedMap, OUTPUT_FILE_NAME_1, TOPK);
		writeToFile(getRankedListPerSearch(MIN_PROB_DIST), OUTPUT_FILE_NAME_2, TOPK);
		writeToFile(getSimilarListPerSearch(rankedMap, SIMILARITY_CUTOFF), OUTPUT_FILE_NAME_3, TOPK);
	}
	
	@Override
	public Set<String> getSrcSet() {
		Set<String> srcClassSet = new HashSet<String>();
		
		try
		{
			File file = new File(PackageSummaryCrawler.MIDP_PKG_DMP_FILE);
		    FileInputStream f = new FileInputStream(file);
		    ObjectInputStream s = new ObjectInputStream(f);
		    @SuppressWarnings("unchecked")
			HashMap<String, String> pageMap = (HashMap<String, String>) s.readObject();
		    srcClassSet = pageMap.keySet();
			s.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    return srcClassSet;
	}
	
	@Override
	public boolean inclusionCriteria(String str) {
		return true;
	}

}
