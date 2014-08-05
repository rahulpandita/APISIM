package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class TopicModel {

	public static void main(String[] args) throws Exception {

		InstanceList instances = createInstanceList(Configuration.ANDROID_DUMP_PATH,Configuration.MIDP_DUMP_PATH);

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		int numTopics = 200;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(5000);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();

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

		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) 
		{
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) 
			{
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
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

		// Create a new instance named "test instance" with empty target and
		// source fields.
		InstanceList testing = createInstanceList(Configuration.ANDROID_DUMP_PATH,Configuration.MIDP_DUMP_PATH);
		

		TopicInferencer inferencer = model.getInferencer();
		Map<Integer,List<IDSorter>> finalMap = new LinkedHashMap<Integer, List<IDSorter>>();
		for(int i=0; i<numTopics; i++)
		{
			finalMap.put(i, new ArrayList<IDSorter>());
		}
		double threshold = 0.05;
		double[] testProbabilities;
		for(int idx=0;idx<testing.size();idx++)
		{
			Instance testInstance = testing.get(idx);
			testProbabilities = inferencer.getSampledDistribution(testInstance, 100, 1, 5);
			for(int i=0; i<numTopics;i++)
			{
				if(testProbabilities[i]>threshold)
					finalMap.get(i).add(new IDSorter(idx, testProbabilities[i]));
			}
		}
		
		writeToFile(finalMap,testing);
		//double[] testProbabilities = inferencer.getSampledDistribution(
		//		testing.get(0), 10, 1, 5);
		//inferencer.writeInferredDistributions(testing, new File("tst.txt"), 100, 1, 5, 0.05, 200);
		//System.out.println("0\t" + testProbabilities[0]);
	}

	private static void writeToFile(Map<Integer, List<IDSorter>> finalMap, InstanceList testing) {
		try {
			PrintWriter out = new PrintWriter(new File("test1.txt"));
			List<IDSorter> lst;
			IDSorter[] sortedArray;
			for(Integer key: finalMap.keySet())
			{
				out.println(key);
				lst = finalMap.get(key);
				sortedArray = new IDSorter[lst.size()];
				sortedArray = lst.toArray(sortedArray);
				Arrays.sort(sortedArray);
				for (int i = 0; (i < sortedArray.length && i<25); i++) {
					out.println("\t(" + sortedArray[i].getWeight()+ ")\t" + testing.get(sortedArray[i].getID()).getSource() );
				}
				out.println("_________________________________________________________________________________");
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static InstanceList createInstanceList(String... fileNames)
			throws UnsupportedEncodingException, FileNotFoundException {
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File("data\\en.txt"),"UTF-8", false, false, false));
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		// Reader fileReader = new InputStreamReader(new FileInputStream(new
		// File("data\\ap.txt")), "UTF-8");
		// instances.addThruPipe(new CsvIterator(fileReader,
		// Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
		// 3, 2, 1)); // data, label, name fields

		instances.addThruPipe(getDataList(fileNames).iterator()); // data,
																	// label,
																	// name
																	// fields
		return instances;
	}

	private static List<Instance> getDataList(String... fileNames) {
		List<Instance> dataList = new ArrayList<Instance>();
		List<APIType> clazzList = new ArrayList<>();
		for (String fileName : fileNames)
			clazzList.addAll(AllClassCrawler.read(fileName));
		// clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		// clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		// clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));

		for (APIType type : clazzList) {
			dataList.add(new DataInstance(type.getSummary(), type.getApiName()+":"+type.getPackage()+"."+type.getName()));
		}
		return dataList;
	}

}
