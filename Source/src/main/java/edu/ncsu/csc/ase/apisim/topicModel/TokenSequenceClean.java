package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureSequenceWithBigrams;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class TokenSequenceClean extends Pipe implements Serializable
{
	boolean markDeletions = false;
	
	HashSet<String> stoplist = new HashSet<String>();
	public static Map<String, IDSorter> termFreqMap = new HashMap<>();
	/**
	 *  Load a stoplist from a file.
	 *  @param stoplistFile    The file to load
	 *  @param encoding        The encoding of the stoplist file (eg UTF-8)
	 *  @param includeDefault  Whether to include the standard mallet English stoplist
	 */
	public TokenSequenceClean(boolean markDeletions) {
		this.markDeletions = markDeletions;
		populate();
	}

	public TokenSequenceClean setMarkDeletions (boolean flag)
	{
		this.markDeletions = flag;
		populate();
		return this;
	}
	
	private void populate()
	{
		try {

			BufferedReader input = null;
			input = new BufferedReader (new FileReader ("data\\stopwordsNew.txt"));
			String line;

			while (( line = input.readLine()) != null) {
				String[] words = line.split ("\\s+");
				for (int i = 0; i < words.length; i++)
				{
					if(!stoplist.contains(words[i]))
						stoplist.add (words[i]);
				}
			}
			input.close();

		} catch (IOException e) {
			throw new IllegalArgumentException("Trouble reading file "+"stopwords.txt");
		}
	}
	public Instance pipe (Instance carrier)
	{
		TokenSequence ts = (TokenSequence) carrier.getData();
		// xxx This doesn't seem so efficient.  Perhaps have TokenSequence
		// use a LinkedList, and remove Tokens from it? -?
		// But a LinkedList implementation of TokenSequence would be quite inefficient -AKM
		TokenSequence ret = new TokenSequence ();
		Token prevToken = null;
		IDSorter termFreq;
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			if(termFreqMap.containsKey(t.getText()))
				termFreq =  termFreqMap.get(t.getText());
			else
				termFreq = new IDSorter(t.getText().hashCode(), 0);
			
			termFreq.set(termFreq.getID(), termFreq.getWeight()+1);
			termFreqMap.put(t.getText(), termFreq);
			if (! ((t.getText().length()>13)||(t.getText().toLowerCase().contains("."))||(t.getText().toLowerCase().contains("("))||(t.getText().toLowerCase().contains(")"))||(t.getText().toLowerCase().contains("_"))||(t.getText().toLowerCase().contains(","))||(t.getText().toLowerCase().contains("*"))||(t.getText().toLowerCase().contains(":"))||(t.getText().toLowerCase().contains(";"))||(t.getText().toLowerCase().contains("%")))) {
				// xxx Should we instead make and add a copy of the Token?
				ret.add (t);
				prevToken = t;
			}
			else if (markDeletions && prevToken != null)
				prevToken.setProperty (FeatureSequenceWithBigrams.deletionMark, t.getText());
		}
		carrier.setData(ret);
		return carrier;
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 2;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeBoolean(markDeletions);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		if (version > 0)
			markDeletions = in.readBoolean();
	}
}