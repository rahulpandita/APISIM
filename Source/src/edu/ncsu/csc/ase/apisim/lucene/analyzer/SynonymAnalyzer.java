package edu.ncsu.csc.ase.apisim.lucene.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.analysis.util.CharArraySet;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

/**
 * A customized synonym analyzer to store
 * 
 * @author Raoul Jetley
 */
public class SynonymAnalyzer extends Analyzer {

	private SynonymMap synMap = null;
	
	/**
	 * Default constructor Initializes the child analyzer to StandardAnalyzer
	 * Initializes the synonym map to read from the dictionary specified in the
	 * configuration file Initializes the maximum number of synonyms to 10
	 * 
	 * @throws IOException
	 */
	public SynonymAnalyzer() throws IOException {
		WordnetSynonymParser parser = new WordnetSynonymParser(true, true,
				new StandardAnalyzer(Configuration.LUCENE_VERSION,CharArraySet.EMPTY_SET));
		try 
		{
			parser.parse(new InputStreamReader(new FileInputStream(Configuration.DICTIONARY)));
			synMap = parser.build();
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Parameterized constructor
	 * 
	 * @param child
	 * @param synMap
	 * @param maxSynonyms
	 */
	public SynonymAnalyzer(Analyzer child, SynonymMap synMap) {
		if (synMap == null)
			throw new IllegalArgumentException("Synonyms must not be null");
		else
			this.synMap = synMap;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new ClassicTokenizer(Configuration.LUCENE_VERSION, reader);
		TokenStream filter = new StandardFilter(Configuration.LUCENE_VERSION, source);
		filter = new LowerCaseFilter(Configuration.LUCENE_VERSION, filter);
		filter = new SynonymFilter(filter, synMap, true);
		
		
		return new TokenStreamComponents(source, filter);
	}

	// Test Method to be removed
	public static void main(String[] args) {
		try {
			SynonymAnalyzer synanAnalyzer = new SynonymAnalyzer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
