package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.wnsim.WNSim;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class SynAnalyser {
	public static void main(String[] args) throws Exception {
		Map<String, String> dict = new LinkedHashMap<String, String>();
		WNSim wnsim = WNSim.getInstance(Configuration.WORD_NET_PATH, Configuration.WORD_NET_CONFIG_XML_PATH);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader("tpmdling/Alphabet.txt"));
		String line = null;
		List<String> alpLst = new ArrayList<String>(); 
		while ((line = reader.readLine()) != null) {
			alpLst.add(line.trim());
		}
		reader.close();
		//String t;
		
		
		for(String t: alpLst)
		{
			System.out.println(t);
			dict.put(t, t);
			for(String key: dict.keySet())
			{
				System.out.println(t + ":" + key);
				
				if(wnsim.getWupSimilarity(key, t)>=0.9)
				{
					dict.put(t, key);
					break;
				}
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("tpmdling/AlphabetSyn.txt")));
		for(String key: dict.keySet())
		{
			writer.append(key +"\t" + dict.get(key));
			writer.append("\n");
		}
		writer.close();
	}
}
