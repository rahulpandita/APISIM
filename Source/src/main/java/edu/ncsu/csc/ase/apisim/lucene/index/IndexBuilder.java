package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;

public class IndexBuilder 
{
	public static void main(String[] args) {
		//buildStdMtdIndexes();
		buildStdMtdIndexesAndroid();
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
	
	static void buildStdMtdIndexesAndroid()
	{
		try 
		{
			//BUILD Standard Index
			Indexer<?> idxr = new APIMtdIndexer(Configuration.PROJECT_PATH + "idx" + File.separator + "tmp");
			idxr.rebuildIndexes();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
