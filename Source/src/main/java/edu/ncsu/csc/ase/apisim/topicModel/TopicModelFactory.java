package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Maths;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.EvalMode;

/**
 * Code Adapted from
 * <a href="http://mallet.cs.umass.edu/topics-devel.php">Mallet Topic Modeling
 * Tutorial</a>
 * 
 * @author Rahul Pandita
 *
 */
public abstract class TopicModelFactory {

	public double MIN_PROB_DIST = 0.001;

	public int TOPK = 50;

	public int SIMILARITY_CUTOFF = 15;

	private int numTopics = 1000;

	private int numThreads = 5;

	private int numIterations = 20000 / 4;

	private int numIterations_Infer = 100;

	private TopicInferencer inferencer;

	private InstanceList instances;

	private Map<Integer, String> topicMap;

	public static final String OUTPUT_FILE_PREFIX = "tpmdling" + File.separator;

	public static final String OUTPUT_FILE_SUFFIX = ".txt";

	private EvalMode eval_mode = EvalMode.UNKNOWN;

	public TopicModelFactory(int numTopic, int numThreads, int numIteration, EvalMode mode) throws Exception {
		this.numTopics = numTopic;
		this.numThreads = numThreads;
		this.numIterations = numIteration;
		if (mode.equals(EvalMode.UNKNOWN))
			throw new IllegalArgumentException("Unrecognized EvalMode");
		this.eval_mode = mode;
		createTopicModel();
	}

	public TopicModelFactory(EvalMode mode) throws Exception {
		if (mode.equals(EvalMode.UNKNOWN))
			throw new IllegalArgumentException("Unrecognized EvalMode");
		this.eval_mode = mode;
		createTopicModel();
	}

	public EvalMode getEval_mode() {
		return eval_mode;
	}

	/**
	 * Creates a parallel Topic Model
	 * 
	 * @throws Exception
	 */
	private void createTopicModel() throws Exception {
		instances = getInstanceList();
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
		model.addInstances(instances);
		model.setNumThreads(numThreads);
		model.setNumIterations(numIterations);
		// model.o
		model.estimate();
		inferencer = model.getInferencer();

		// populate TopicMap
		topicMap = new TreeMap<Integer, String>();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		StringBuffer buff;
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			int rank = 0;
			buff = new StringBuffer();
			buff.append(topic);
			buff.append(" : ");
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				buff.append(instances.getDataAlphabet().lookupObject(idCountPair.getID()));
				buff.append(" ");
			}
			topicMap.put(topic, buff.toString());
		}

	}

	/**
	 * This method will return the object of {@link InstanceList} containing the
	 * data to create a {@code Topic Model}
	 * 
	 * @return {@link InstanceList}
	 */
	public abstract InstanceList getInstanceList();

	/**
	 * This method will return the object of {@link InstanceList} containing the
	 * data to query a {@code Topic Model} to produce Jenson Shanon
	 * Distribution. {@code Source} and {@code Target} Data
	 * 
	 * @return {@link InstanceList} Typically a combination of
	 *         {@code getInstanceList()} and {@code getSearchInstanceList()}
	 */
	public abstract InstanceList getTargetInstanceList();

	/**
	 * This method will return the object of {@link InstanceList} containing the
	 * data to query a {@code Topic Model} for custom Similarity. {@code Source}
	 * and {@code Target} Data
	 * 
	 * @return {@link InstanceList}
	 */
	public abstract InstanceList getSearchInstanceList();

	/**
	 * This method will return the object of {@link InstanceList} containing the
	 * data to query a {@code Topic Model} for custom Similarity. {@code Source}
	 * and {@code Target} Data
	 * 
	 * @return {@link InstanceList}
	 */
	public abstract InstanceList getSInstanceList();

	public final void eval() throws Exception {
		Long startTime = System.currentTimeMillis();
		runEval();

		long seconds = Math.round((System.currentTimeMillis() - startTime) / 1000.0);
		long minutes = seconds / 60;
		seconds %= 60;
		long hours = minutes / 60;
		minutes %= 60;
		long days = hours / 24;
		hours %= 24;

		StringBuilder timeReport = new StringBuilder();
		timeReport.append("\nTotal time: ");
		if (days != 0) {
			timeReport.append(days);
			timeReport.append(" days ");
		}
		if (hours != 0) {
			timeReport.append(hours);
			timeReport.append(" hours ");
		}
		if (minutes != 0) {
			timeReport.append(minutes);
			timeReport.append(" minutes ");
		}
		timeReport.append(seconds);
		timeReport.append(" seconds");

		System.out.println(timeReport.toString());
	}

	public abstract void runEval() throws Exception;

	/**
	 * Utility Function to write results to a text file
	 * 
	 * @param finalMap
	 * @param targetInstances
	 * @param fileID
	 */
	protected void writeToFile(Map<String, List<String>> finalMap, String fileID, int topK) {
		String fileName = OUTPUT_FILE_PREFIX + fileID + OUTPUT_FILE_SUFFIX;
		try {
			BufferedWriter out = new BufferedWriter(new PrintWriter(new FileOutputStream(fileName), true));
			List<String> lst;
			for (Object key : finalMap.keySet()) {
				out.write(key.toString());
				out.newLine();
				lst = finalMap.get(key);
				for (int i = 0; (i < lst.size() && i < topK); i++) {
					out.write("\t" + lst.get(i));
					out.newLine();
				}
				out.write("_________________________________________________________________________________");
				out.newLine();
			}
			out.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Returns a Map containing the ranked List of the documents per Topic
	 * 
	 * @param threshold
	 * @return
	 */
	public final Map<String, List<String>> getRankedListPerTopic(double threshold) {

		Map<String, List<IDSorter>> returnMap = new LinkedHashMap<String, List<IDSorter>>();
		double[] testProbabilities;
		Instance testInstance;
		InstanceList instances = getTargetInstanceList();
		for (int idx = 0; idx < instances.size(); idx++) {
			testInstance = instances.get(idx);
			testProbabilities = inferencer.getSampledDistribution(testInstance, numIterations_Infer, 1, 5);
			for (int i = 0; i < testProbabilities.length; i++) {

				if (testProbabilities[i] > threshold) {
					if (!returnMap.containsKey(topicMap.get(i)))
						returnMap.put(topicMap.get(i), new ArrayList<IDSorter>());

					returnMap.get(topicMap.get(i)).add(new IDSorter(idx, testProbabilities[i]));
				}
			}
		}
		return getFormattedMap(returnMap, instances);
	}

	/**
	 * Returns an ordered List of documents that match search Documents
	 * 
	 * @param threshold
	 * @return
	 */
	public final Map<String, List<String>> getRankedListPerSearch(double threshold) {
		Map<String, List<IDSorter>> returnMap = new LinkedHashMap<String, List<IDSorter>>();
		InstanceList searchList = getSearchInstanceList();

		InstanceList targetList = getSInstanceList();
		Map<Instance, double[]> cmpMap = new HashMap<Instance, double[]>();
		for (Instance ins : targetList) {
			// TODO get to know parameters better
			cmpMap.put(ins, inferencer.getSampledDistribution(ins, 100, 1, 5));
		}

		double[] searchProb;
		Instance ins, searchInstance;
		double distance;
		String key;

		for (int srcIdx = 0; srcIdx < searchList.size(); srcIdx++) {
			searchInstance = searchList.get(srcIdx);
			searchProb = inferencer.getSampledDistribution(searchInstance, 100, 1, 5);

			key = (String) searchInstance.getSource() + "[" + srcIdx + "]";

			returnMap.put(key, new ArrayList<IDSorter>());

			for (int idx = 0; idx < targetList.size(); idx++) {
				ins = targetList.get(idx);
				distance = Maths.jensenShannonDivergence(searchProb, cmpMap.get(ins));
				if (distance >= threshold)
					returnMap.get(key).add(new IDSorter(idx, distance));
			}

		}

		return getFormattedMap(returnMap, targetList);
	}

	public final Map<String, List<String>> getSimilarListPerSearch(Map<String, List<String>> finalMap, int topK) {
		Map<String, List<String>> returnMap = new TreeMap<String, List<String>>();
		Set<String> srcClassSet = getSrcSet();
		List<String> lst;
		Boolean flag = false;
		Map<String, IDSorter> tgtPkgMap;
		List<String> vocabList, tgtPkgPerTopicList;
		String tmp;
		IDSorter id;
		double weight;
		for (String pkgStr : srcClassSet) {
			tgtPkgMap = new LinkedHashMap<String, IDSorter>();
			vocabList = new ArrayList<String>();
			for (String key : finalMap.keySet()) {
				tgtPkgPerTopicList = new ArrayList<String>();
				flag = false;
				lst = finalMap.get(key);
				for (int i = 0; (i < lst.size() && i < topK); i++) {
					tmp = lst.get(i).split("\t")[1];
					if (lst.get(i).endsWith(pkgStr)) {
						flag = true;
						// break;
					}
					if (inclusionCriteria(tmp))
						tgtPkgPerTopicList.add(tmp);
				}
				if (flag) {
					tgtPkgPerTopicList.removeAll(srcClassSet);

					for (String str : tgtPkgPerTopicList) {
						weight = Math
								.log(1.0 + (Double.valueOf(topK) - Double.valueOf(tgtPkgPerTopicList.indexOf(str)))); // /Double.valueOf(topK));

						if (!tgtPkgMap.containsKey(str)) {
							vocabList.add(str);
							tgtPkgMap.put(str, new IDSorter(vocabList.indexOf(str), 0));

						}

						id = tgtPkgMap.get(str);
						id.set(id.getID(), id.getWeight() + weight);
					}
				}
			}
			IDSorter[] sortedArray = new IDSorter[tgtPkgMap.values().size()];
			sortedArray = tgtPkgMap.values().toArray(sortedArray);
			Arrays.sort(sortedArray);
			List<String> resultList = new ArrayList<String>();
			for (int i = 0; (i < sortedArray.length); i++) {
				resultList.add(vocabList.get(sortedArray[i].getID()) + "\t(" + sortedArray[i].getWeight() + ")");
			}
			returnMap.put(pkgStr, resultList);
		}
		return returnMap;
	}

	public abstract boolean inclusionCriteria(String str);

	public abstract Set<String> getSrcSet();

	protected Map<String, List<String>> getFormattedMap(Map<?, List<IDSorter>> finalMap, InstanceList instanceList) {
		Map<String, List<String>> returnMap = new TreeMap<String, List<String>>();
		List<IDSorter> lst;
		List<String> resultList;
		for (Object key : finalMap.keySet()) {
			resultList = new ArrayList<String>();
			lst = finalMap.get(key);
			sort(lst);
			for (IDSorter resultObj : lst) {
				System.out
						.println("(" + resultObj.getWeight() + ")\t" + instanceList.get(resultObj.getID()).getSource());
				resultList.add("(" + resultObj.getWeight() + ")\t" + instanceList.get(resultObj.getID()).getSource());
			}
			returnMap.put(key.toString(), resultList);
		}
		return returnMap;
	}

	@SuppressWarnings("unchecked")
	private void sort(List<IDSorter> lst) {
		Collections.sort(lst);
	}

	protected void abc(Map<String, List<String>> finalMap, String fileID, int topK) {
		String fileName = OUTPUT_FILE_PREFIX + fileID + 1 + OUTPUT_FILE_SUFFIX;
		try {
			PrintWriter out = new PrintWriter(new File(fileName));
			List<String> lst;
			StringBuffer buff;
			Boolean flag = false;
			for (Object key : finalMap.keySet()) {
				buff = new StringBuffer();
				flag = false;
				buff.append(key);
				buff.append("\n");
				lst = finalMap.get(key);
				for (int i = 0; (i < lst.size() && i < topK); i++) {
					if (lst.get(i).endsWith("javax.microedition.lcdui")) {
						flag = true;
						break;
					}
					buff.append("\t" + lst.get(i));
					buff.append("\n");
				}
				buff.append("_________________________________________________________________________________");
				buff.append("\n");
				if (flag)
					out.write(buff.toString());
			}
			out.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}
}
