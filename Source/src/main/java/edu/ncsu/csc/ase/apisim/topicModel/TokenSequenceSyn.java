package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.didion.jwnl.JWNLException;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import edu.illinois.cs.cogcomp.wnsim.WNSim;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class TokenSequenceSyn extends Pipe implements Serializable
{
	boolean markDeletions = false;
	
	private static Map<String, String> dict = new HashMap<String, String>();
	
	private static WNSim wnsim;
	
	/**
	 *  Load a stoplist from a file.
	 *  @param stoplistFile    The file to load
	 *  @param encoding        The encoding of the stoplist file (eg UTF-8)
	 *  @param includeDefault  Whether to include the standard mallet English stoplist
	 */
	public TokenSequenceSyn(boolean markDeletions) {
		try {
			wnsim = WNSim.getInstance(Configuration.WORD_NET_PATH,
					Configuration.WORD_NET_CONFIG_XML_PATH);
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.markDeletions = markDeletions;
	}

	public TokenSequenceSyn setMarkDeletions (boolean flag)
	{
		this.markDeletions = flag;
		return this;
	}

	public Instance pipe (Instance carrier)
	{
		TokenSequence ts = (TokenSequence) carrier.getData();
		// xxx This doesn't seem so efficient.  Perhaps have TokenSequence
		// use a LinkedList, and remove Tokens from it? -?
		// But a LinkedList implementation of TokenSequence would be quite inefficient -AKM
		//TokenSequence ret = new TokenSequence ();
		Token t;
		boolean flag= false;
		for (int i = 0; i < ts.size(); i++) {
			t = ts.get(i);
			if(dict.containsKey(t.getText()))
				t.setText(dict.get(t.getText()));
			else
			{
				for(String key: dict.keySet())
				{
					try 
					{
						if(wnsim.getWupSimilarity(key, t.getText())>=0.9)
						{
							flag = true;
							t.setText(key);
							break;
						}
					} catch (JWNLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(!flag)
				{
					dict.put(t.getText(), t.getText());
				}
			}
		}
		carrier.setData(ts);
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