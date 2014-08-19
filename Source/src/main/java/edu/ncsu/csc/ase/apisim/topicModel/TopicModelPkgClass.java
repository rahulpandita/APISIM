package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.PackageSummaryCrawler;

public class TopicModelPkgClass extends TopicModelFactory {

	public static String OUTPUT_FILE_NAME_1 = "PkgClassSummaryPerTopic";

	public static String OUTPUT_FILE_NAME_2 = "PkgClassSummaryPerPkg";
	
	public static String OUTPUT_FILE_NAME_3 = "PkgClassSummaryPerPkgSimilarity";

	public static void main(String[] args) throws Exception{
		TopicModelFactory tm;
		for(int i =0; i<20; i++)
		{
		
			tm = new TopicModelPkgClass(i);
			tm.runEval();
		}
	}

	public TopicModelPkgClass(int i) throws Exception {
		super(500, 2, 5000);
		OUTPUT_FILE_NAME_1 =OUTPUT_FILE_NAME_1+i;
		OUTPUT_FILE_NAME_2 = OUTPUT_FILE_NAME_2 +i;
		OUTPUT_FILE_NAME_3 = OUTPUT_FILE_NAME_3 +i;
	}

	@Override
	public InstanceList getInstanceList() {
		try {
			return InstanceCreator.createInstanceListandroidpkgClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try {
			return InstanceCreator.createInstanceListPkgCls();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try {
			return InstanceCreator.createInstanceListmidppkgClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void runEval() throws Exception {
		//writeToFile(getRankedListPerTopic(0.01), OUTPUT_FILE_NAME_1, 10);
		//writeToFile(getRankedListPerSearch(0.01), OUTPUT_FILE_NAME_2, 10);
		writeToFile(getSimilarListPerSearch(getRankedListPerTopic(0.01), 10),OUTPUT_FILE_NAME_3, Integer.MAX_VALUE);
		
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