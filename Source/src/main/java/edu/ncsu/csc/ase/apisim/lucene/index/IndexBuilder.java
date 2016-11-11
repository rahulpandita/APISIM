package edu.ncsu.csc.ase.apisim.lucene.index;



import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.tartarus.snowball.ext.PorterStemmer;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;

public class IndexBuilder 
{
	public static void main(String[] args) {
		
		
		//buildStdMtdIndexes(APITYPE.ANDROID);
		//buildStdMtdIndexes(APITYPE.CLDC);
		//buildStdMtdIndexes(APITYPE.DOTNET);
		//buildStdMtdIndexes(APITYPE.ECLIPSE);
		//buildStdMtdIndexes(APITYPE.JAVA);
		buildStdMtdIndexes(APITYPE.MIDP);
		//buildStdMtdIndexes(APITYPE.UNKNOWN);
		
	}
	
	static void tst(String word)
	{
		PorterStemmer stem = new PorterStemmer();
		stem.setCurrent(word);
		 stem.stem();
		 String result = stem.getCurrent();
		System.out.println(result);
	}
	static void tst1(String word)
	{
		EnglishAnalyzer en_an = new EnglishAnalyzer(Configuration.LUCENE_VERSION);
		QueryParser parser = new QueryParser(Configuration.LUCENE_VERSION, "$", en_an);
		try {
			System.out.println("result: " + parser.parse(word).toString("$"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void buildAll()
	{
		try 
		{
			//BUILD Standard Index
			Indexer<?> idxr = new APIClassIndexer();
			idxr.rebuildIndexes();
			
			//BUILD INDEX Based on Synonym Analyzer
			idxr = new APIClassIndexer();
			idxr.setAnalyser(new SynonymAnalyzer());
			idxr.setIdx_path(Configuration.API_IDX_CLAZZ_SYN);
			idxr.rebuildIndexes();
			
			//BUILD Standard Method Indexes
			idxr = new APIMtdIndexer();
			idxr.rebuildIndexes();
			
			//BUILD Method INDEX Based on Synonym Analyzer
			idxr = new APIMtdIndexer();
			idxr.analyser = new SynonymAnalyzer();
			idxr.idx_path = Configuration.API_IDX_FILE_SYNONYM;
			idxr.rebuildIndexes();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void buildStdMtdIndexes()
	{
		try 
		{
			//BUILD Standard Index
			Indexer<?> idxr = new APIMtdIndexer();
			idxr.rebuildIndexes();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void buildStdMtdIndexes(APITYPE type)
	{
		try 
		{
			//BUILD Standard Index
			Indexer<?> idxr = new APIMtdIndexer(type);
			idxr.rebuildIndexes();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
