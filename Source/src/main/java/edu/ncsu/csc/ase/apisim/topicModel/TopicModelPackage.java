package edu.ncsu.csc.ase.apisim.topicModel;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.PackageSummaryCrawler;

public class TopicModelPackage extends TopicModelFactory {

	public static final String OUTPUT_FILE_NAME_1 = "PkgSummaryPerTopic";

	public static final String OUTPUT_FILE_NAME_2 = "PkgSummaryPerPkg";

	public static final String OUTPUT_FILE_NAME_3 = "PkgSummaryPerPkgSimilarity";
	
	public static void main(String[] args) {
		TopicModelFactory tm;
		try {
			tm = new TopicModelPackage();
			tm.runEval();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TopicModelPackage() throws Exception {
		super(500, 2, 10000);

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
		writeToFile(getRankedListPerTopic(0.01), OUTPUT_FILE_NAME_1, 30);
		writeToFile(getRankedListPerSearch(0.05), OUTPUT_FILE_NAME_2, 30);
		writeToFile(getSimilarListPerSearch(getRankedListPerTopic(0), 30), OUTPUT_FILE_NAME_3, 30);
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
