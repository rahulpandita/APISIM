package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity
 * project
 * 
 * @author Rahul Pandita
 * 
 */
public abstract class PremEval<T> {
	protected Analyzer analyser;

	private String idxLoc = "";
	
	protected int hitsPerPage = 10;
	
 	public PremEval(String idxFileLoc) {
		initialize(idxFileLoc,
				new EnglishAnalyzer(Configuration.LUCENE_VERSION));
	}

	public PremEval(String idxFileLoc, Analyzer analyzer) {
		initialize(idxFileLoc, analyzer);
	}

	private void initialize(String idxFileLoc, Analyzer analyzer) {
		this.analyser = analyzer;
		this.idxLoc = idxFileLoc;
	}

	private List<Query> createQueryList(List<Occur[]> clauseVecList,
			List<String[]> termVecList, List<String[]> columnVecList)
			throws ParseException {
		if ((clauseVecList.size() != termVecList.size())
				&& (clauseVecList.size() != columnVecList.size()))
			throw new IllegalArgumentException(
					"Clause, Term, and Column Vector Lists are not of equal size");
		List<Query> queryList = new ArrayList<Query>();
		for (int i = 0; i < clauseVecList.size(); i++) {
			if ((clauseVecList.get(i).length != termVecList.get(i).length)
					&& (clauseVecList.get(i).length != columnVecList.get(i).length))
				throw new IllegalArgumentException(
						"Clause, Term, and Column Vectors are not of equal size at index - "
								+ i);
			queryList.add(createQuery(clauseVecList.get(i), termVecList.get(i),
					columnVecList.get(i)));
		}
		return queryList;
	}

	private Query createQuery(Occur[] clauseVector, String[] termVector,
			String[] columnVector) throws ParseException {
		Query query = null;
		try
		{
		query = MultiFieldQueryParser.parse(Configuration.LUCENE_VERSION,
				termVector, columnVector, clauseVector, analyser);
		}
		catch(Exception e)
		{
			System.out.println(e);
			//TODO  Fix for empty search Str
		}
		return query;
	}

	public final Map<String, Map<T, List<Document>>> eval() throws Exception {
		Map<String, Map<T, List<Document>>> resultMap = new HashMap<String, Map<T, List<Document>>>();

		Map<String, List<T>> includedMap = inclusionList();
		for (String page : includedMap.keySet()) {
			List<T> argList = includedMap.get(page);
			Map<T, List<Document>> objResMap = new HashMap<T, List<Document>>();
			for (T obj : argList) {
				List<Query> queryList = createQueryList(getClauseVector(),
						getTermVector(obj), getColumnVector());
				List<Document> docList = customRanking(queryList);
				objResMap.put(obj, docList);
			}
			resultMap.put(page, objResMap);
		}

		return resultMap;
	}

	protected final List<Document> searcher(Query query) throws Exception {
		// System.err.println("here");
		List<Document> retList = new ArrayList<>();
		Directory index = FSDirectory.open(new File(idxLoc));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(query, collector);
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) 
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			d.add(new FloatField("SCORE", hits[i].score, Field.Store.NO));
			retList.add(d);
		}
		return retList;
	}

	public abstract List<Document> customRanking(List<Query> queryList)
			throws Exception;

	public abstract Map<String, List<T>> inclusionList();

	public abstract List<Occur[]> getClauseVector();

	public abstract List<String[]> getColumnVector();

	public abstract List<String[]> getTermVector(T arg);

}
