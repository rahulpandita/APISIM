package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class TermFreqEval 
{
	public static void main(String[] args) throws Exception {
		TermFreqEval eval  = new TermFreqEval();
		Query[] query = eval.createQueries();
		for(Query q:query)
		{
			eval.abc(eval.searcher(q));
		}
		
	}
	
	private Query[] createQueries() {
		BooleanQuery query1 =  new BooleanQuery();
		query1.add(new TermQuery(new Term(Configuration.IDX_FIELD_API_NAME,"android")), Occur.MUST);
		
		BooleanQuery query2 =  new BooleanQuery();
		query2.add(new TermQuery(new Term(Configuration.IDX_FIELD_API_NAME,"android")), Occur.MUST_NOT);
		
		
		return new Query[]{query1,query2};
	}

	private ScoreDoc[] searcher(Query query) throws Exception {
		// System.err.println("here");
		Directory index = FSDirectory.open(new File(Configuration.API_IDX_FILE));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TotalHitCountCollector collector1 = new TotalHitCountCollector();
		searcher.search(query, collector1);
		
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(collector1.getTotalHits(), true);
		searcher.search(query, collector);
		return collector.topDocs().scoreDocs;
		
	}
	
	private void abc(ScoreDoc[] hits) throws Exception
	{
		Directory index = FSDirectory.open(new File(Configuration.API_IDX_FILE));
		IndexReader reader = DirectoryReader.open(index);
		Terms terms;
		//Term term;
		
		for (int i = 0; i < hits.length; ++i) 
		{
			int docId = hits[i].doc;
			terms  = reader.getTermVector(docId, Configuration.IDX_FIELD_DESCRIPTION);
			
			System.out.println(terms);
			
		}
		
	}	
	
}
