package edu.ncsu.csc.ase.apisim.topicModel;


import cc.mallet.types.InstanceList;

public class TopicModelPackage extends TopicModelFactory {

	public static final String OUTPUT_FILE_NAME_1 = "PkgSummaryPerTopic";

	public static final String OUTPUT_FILE_NAME_2 = "PkgSummaryPerPkg";

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
		super(500, 2, 2000);

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
		//Map<String, List<String>> mp = getRankedListPerSearch(0)
		writeToFile(getRankedListPerTopic(0.01),
				OUTPUT_FILE_NAME_1, 30);
		writeToFile(getRankedListPerSearch(0.05), OUTPUT_FILE_NAME_2, 30);
		abc(getRankedListPerTopic(0),OUTPUT_FILE_NAME_1, 30);
	}

}
