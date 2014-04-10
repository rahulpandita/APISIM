package edu.ncsu.csc.ase.apisim.lucene.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;













import edu.ncsu.csc.ase.apisim.configuration.Configuration;


/**
 * A customized synonym analyzer to store 
 * @author Raoul Jetley
 */
public class SynonymAnalyzer extends Analyzer {

	private SynonymMap synMap = null; 
	private int maxSynonyms = 0;
	
	/**
     * Default constructor
     * Initializes the child analyzer to StandardAnalyzer
     * Initializes the synonym map to read from the dictionary specified in the configuration file
     * Initializes the maximum number of synonyms to 10 
	 * @throws IOException 
     */
    public SynonymAnalyzer() throws IOException {
    	WordnetSynonymParser parser = new WordnetSynonymParser(true, true, new StandardAnalyzer(Version.LUCENE_47,CharArraySet.EMPTY_SET));
    	try 
    	{
			parser.parse(new InputStreamReader(new FileInputStream(Configuration.DICTIONARY)));
			synMap = parser.build();
	    	maxSynonyms = 10;
		} 
    	catch (ParseException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    /**
     * Parameterized constructor
     * @param child
     * @param synMap
     * @param maxSynonyms
     */
    public SynonymAnalyzer(Analyzer child, SynonymMap synMap, int maxSynonyms) {
    	if (synMap == null)
    		throw new IllegalArgumentException("Synonyms must not be null");
    	else this.synMap = synMap;
    	if (maxSynonyms < 0) 
    		throw new IllegalArgumentException("The number of synonyms cannot be negative");
    	else this.maxSynonyms = maxSynonyms;
    }

	@SuppressWarnings("resource")
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new ClassicTokenizer(Version.LUCENE_47, reader);
	    TokenStream filter = new StandardFilter(Version.LUCENE_47, source);
	    filter = new LowerCaseFilter(Version.LUCENE_47,filter);
	    filter = new SynonymFilter(filter, synMap, true);
	    
	    
	    //String synfile="/path/synonyms.txt";
        Map<String,String> filterargs= new HashMap<String, String>();
        filterargs.put("luceneMatchVersion", Version.LUCENE_47.toString());
        filterargs.put("synonyms", Configuration.DICTIONARY);
        filterargs.put("ignoreCase", "true");
        filterargs.put("format", "wordnet");
        filterargs.put("expand", "true");
        SynonymFilterFactory factory =  new SynonymFilterFactory(filterargs);
        
       // filter = factory.create(filter);
        return new TokenStreamComponents(source, filter);
	}
	
	//Test Method to be removed 
	public static void main(String[] args) {
		try {
			SynonymAnalyzer synanAnalyzer = new SynonymAnalyzer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
