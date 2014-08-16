package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import cc.mallet.util.Maths;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.util.FileUtil;

/**
 * Code Adapted from <a href="http://mallet.cs.umass.edu/topics-devel.php">Mallet Topic Modeling Tutorial</a>
 * 
 * @author Rahul Pandita
 *
 */
public class TopicModel {

	public static void main(String[] args) throws Exception {
		main1();
	}
	
	public static void main1() throws Exception {

		InstanceList instances = InstanceCreator.createInstanceList(Configuration.ANDROID_DUMP_PATH,Configuration.MIDP_DUMP_PATH,Configuration.CLDC_DUMP_PATH);
		
		
		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		int numTopics = 500;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(10000);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		tst(dataAlphabet);
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;

		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance,
		// given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		HashMap<Integer, String> topicMap = new HashMap<>();
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) 
		{
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			StringBuffer buff = new StringBuffer();
			while (iterator.hasNext() && rank < 5) 
			{
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
				buff.append(dataAlphabet.lookupObject(idCountPair.getID()));
				buff.append(" ");
				
			}
			topicMap.put(topic, buff.toString());
			System.out.println(out);
		}

		// Create a new instance with high probability of topic 0
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID())
					+ " ");
			rank++;
		}

		TopicInferencer inferencer = model.getInferencer();
		
		Map<String, List<IDSorter>> finalMap1 = getRankedListPerTopic(0.05, inferencer, instances, topicMap);
		
		writeToFile(finalMap1,instances,"tpmdling\\test71.txt");
		
		InstanceList targetList = InstanceCreator.createInstanceList(Configuration.ANDROID_DUMP_PATH);
		
		InstanceList searchList = InstanceCreator.createInstanceList1(InstanceCreator.getEvalList(),Configuration.MIDP_DUMP_PATH,Configuration.CLDC_DUMP_PATH);
		
		Map<String, List<IDSorter>> finalMap = getRankedListPerSearch(0.05, inferencer, targetList, searchList);
		
		writeToFile(finalMap, targetList, "tpmdling\\test72.txt");
//		writeToFile("tpmdling\\freqLst.txt");
		
	}

	/**
	 * @param dataAlphabet
	 */
	private static void tst(Alphabet dataAlphabet) {
		System.out.println(dataAlphabet.size());
		Iterator<?> iter = dataAlphabet.iterator();
		StringBuffer buff = new StringBuffer();
		while(iter.hasNext())
		{
			buff.append(iter.next());
			buff.append("\n");
		}
		FileUtil.writeStringtoFile("tpmdling\\Alphabet.txt", buff.toString());
	}
	
	private static Map<String, List<IDSorter>> getRankedListPerSearch(double d, TopicInferencer inferencer, InstanceList targetList, InstanceList searchList) {
		Map<String,List<IDSorter>> returnMap = new LinkedHashMap<String, List<IDSorter>>();
		double[] srcProb,targetProb;
		Instance srcInstance,targetInstance;
		double distance;
		for(int srcIdx=0; srcIdx<searchList.size();srcIdx++)
		{
			srcInstance = searchList.get(srcIdx);
			srcProb = inferencer.getSampledDistribution(srcInstance, 100, 1, 5);
			returnMap.put((String)srcInstance.getSource(), new ArrayList<IDSorter>());
			for(int idx=0;idx<targetList.size();idx++)
			{
				targetInstance = targetList.get(idx);
				targetProb = inferencer.getSampledDistribution(targetInstance, 100, 1, 5);
				
				distance = Maths.jensenShannonDivergence(srcProb, targetProb);
				if(distance >=d)
				returnMap.get((String)srcInstance.getSource()).add(new IDSorter(idx, distance));
					
			}
		}
		
		return returnMap;
	}

	/**
	 * 
	 * @param threshold
	 * @param inferencer
	 * @param testInsList
	 * @return
	 */
	public static Map<String, List<IDSorter>> getRankedListPerTopic(double threshold, TopicInferencer inferencer, InstanceList testInsList, HashMap<Integer, String> topicMap) {
		
		Map<String ,List<IDSorter>> returnMap = new LinkedHashMap<String, List<IDSorter>>();
		double[] testProbabilities;
		Instance testInstance;
		
		for(int idx=0;idx<testInsList.size();idx++)
		{
			testInstance = testInsList.get(idx);
			testProbabilities = inferencer.getSampledDistribution(testInstance, 100, 1, 5);
			for(int i=0; i<testProbabilities.length;i++)
			{
				
				if(testProbabilities[i]>threshold)
				{
					if(!returnMap.containsKey(topicMap.get(i)))
						returnMap.put(topicMap.get(i), new ArrayList<IDSorter>());
					
					returnMap.get(topicMap.get(i)).add(new IDSorter(idx, testProbabilities[i]));
				}
			}
		}
		return returnMap;
	}
	
	private static void writeToFile(Map<?, List<IDSorter>> finalMap, InstanceList instanceList, String fileName) 
	{
		try 
		{
			PrintWriter out = new PrintWriter(new File(fileName));
			List<IDSorter> lst;
			IDSorter[] sortedArray;
			for(Object key: finalMap.keySet())
			{
				out.println(key.toString());
				lst = finalMap.get(key);
				sortedArray = new IDSorter[lst.size()];
				sortedArray = lst.toArray(sortedArray);
				Arrays.sort(sortedArray);
				for (int i = 0; (i < sortedArray.length && i<30); i++) {
					out.println("\t(" + sortedArray[i].getWeight()+ ")\t" + instanceList.get(sortedArray[i].getID()).getSource() );
				}
				out.println("_________________________________________________________________________________");
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private static void writeToFile(String fileName) 
	{
		try 
		{
			PrintWriter out = new PrintWriter(new File(fileName));
			Map<String, IDSorter> termFreqMap = TokenSequenceClean.termFreqMap;
			IDSorter lst;
			IDSorter[] sortedArray;
			for(String key: termFreqMap.keySet())
			{
				lst = termFreqMap.get(key);
				out.println(key + "\t" + lst.getWeight() );
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
	}
}
