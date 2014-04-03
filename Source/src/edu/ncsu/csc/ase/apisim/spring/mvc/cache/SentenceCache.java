/*package edu.ncsu.csc.ase.apisim.spring.mvc.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import edu.ncsu.csc.ase.dristi.configuration.Config;
import edu.ncsu.csc.ase.dristi.datastructure.Tuple;

public class SentenceCache 
{
	*//**
	 * instance variable for singleton class
	 *//*
	private static SentenceCache instance;
	
	*//**
	 * Object to hold the sentence data
	 *//*
	private Map<String, Tuple> sentenceMap;
	
	*//**
	 * Constructor to create the object
	 * @throws ClassNotFoundException when the cache file is unavailable
	 * @throws IOException error in opening the cache file
	 *//*
	private SentenceCache() throws ClassNotFoundException, IOException
	{
		sentenceMap = new HashMap<String, Tuple>();
		populateSentenceMap();
	}
	
	*//**
	 * populating the cache from previously encountered sentences stored in cache file
	 * @throws ClassNotFoundException when the cache file is unavailable
	 * @throws IOException error in opening the cache file
	 *//*
	private void populateSentenceMap() throws ClassNotFoundException, IOException {
		refreshCache();
		
	}

	*//**
	 * Static method to get the instance variable of Singleton Class Sentence Cache
	 * @return instance of Singleton Class Sentence Cache
	 * @throws ClassNotFoundException when the cache file is unavailable
	 * @throws IOException error in opening the cache file
	 *//*
	public static synchronized SentenceCache getInstace() throws ClassNotFoundException, IOException
	{
		if(instance == null)
		{
			instance = new SentenceCache();
		}
		return instance;
	}
	
	*//**
	 * Method to write the data of current cache to memory. First the old data is flushed
	 * Be careful to backup old Cache before invoking this method.
	 * @throws IOException error in opening the cache file
	 * @throws ClassNotFoundException when the cache file is unavailable
	 *//*
	public void updatePersistance() throws IOException, ClassNotFoundException
	{
		System.err.println("Attempting to write Cache");
		File file = new File(Config.SENTENCE_CACHE_FILE_PATH);  
		if(file.exists())
			file.delete();
		
		file = new File(Config.SENTENCE_CACHE_FILE_PATH);
		FileOutputStream f = new FileOutputStream(file);  
		ObjectOutputStream s = new ObjectOutputStream(f);          
		s.writeObject(sentenceMap);
		s.close();
		System.err.println("Cache Written -> " + file.getAbsolutePath());
	}
	
	*//**
	 * Method to read the data of persistent memory into cache. First the current cache data is flushed
	 * Be careful to backup current Cache before invoking this method.
	 * @throws IOException error in opening the cache file
	 * @throws ClassNotFoundException when the cache file is unavailable
	 *//*
	@SuppressWarnings("unchecked")
	private void refreshCache() throws IOException, ClassNotFoundException
	{
		File file = new File("temp");
	    if(file.exists())
	    {	
			FileInputStream f = new FileInputStream(file);
		    ObjectInputStream s = new ObjectInputStream(f);
		    sentenceMap = (HashMap<String, Tuple>) s.readObject();
		    s.close();
	    }
	    else
	    	sentenceMap = new HashMap<String, Tuple>();
	}
	
	*//**
	 * Add a sentence to current cache,. If the sentence already exists it will not be added
	 * @param sentence
	 * @param t
	 *//*
	public void addSentence(String sentence, Tuple t)
	{
		if(getTuple(sentence)==null)
			sentenceMap.put(sentence, t);
			
	}

	*//**
	 * returns the tuple representation of the sentence.
	 * @param sentence
	 * @return Tuple Representation of sentence, null if none exists
	 *//*
	public Tuple getTuple(String sentence) {
		return sentenceMap.get(sentence);
	}
	
}
*/