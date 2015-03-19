package edu.ncsu.csc.realsearch.apisim.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jsoup.Jsoup;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.realsearch.apisim.cache.Cache;
import edu.ncsu.csc.realsearch.apisim.databeans.SearchResultBean;

public class LuceneQueryHelper {
	private static Analyzer analyser = new EnglishAnalyzer(Configuration.LUCENE_VERSION);
	private static String idxLocSrc = Configuration.PROJECT_PATH + "idx" + File.separator + Cache.src_type;
	private static String idxLoc = Configuration.PROJECT_PATH + "idx" + File.separator + Cache.dest_type;
	
	private static int hitsPerPage = 10;
	
	public static Query getQuery(APIMtd mtd) {
		
		
		
		
		Occur[] clauseVector = {Occur.SHOULD, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		
		String[] columnVector = new String[] {
				Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT,
				Configuration.IDX_FIELD_CLASS_DESCRIPTION,
				Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT,
				Configuration.IDX_FIELD_DESCRIPTION};
		
		String clazzName = MultiFieldQueryParser.escape(mtd.getParentClass().getPackage().replaceAll("\\.", " ") + " " + StringUtil.splitCamelCase(mtd.getParentClass().getName()));
		String clazzDesc = MultiFieldQueryParser.escape(getDescriptionQuery(mtd.getParentClass().getName(), mtd.getParentClass().getSummary()));
		
		clazzName = clazzName.replaceAll(Pattern.quote("javax"), "").trim();
		clazzName = clazzName.replaceAll(Pattern.quote("microedition"), "").trim();
		clazzName = clazzName.replaceAll(Pattern.quote("lcdui"), "").trim();
		clazzName = clazzName.replaceAll(Pattern.quote("java"), "").trim();
		
		String name = getJavaMethodName(mtd.getName());
		if(name==null)
			name = mtd.getName();
		name = name.trim();
		//if(name.toLowerCase().startsWith("get")||(name.toLowerCase().startsWith("set"))||(name.toLowerCase().startsWith("tostring")))
		//{
		//	name = "*";
		//}
		String methodBaseName = MultiFieldQueryParser.escape(StringUtil.splitCamelCase(name));
		String methodDesc = MultiFieldQueryParser.escape(getDescriptionQuery(name,mtd.getDescription()));
		
		
		
		
		String[] termVector = new String[] {clazzName, clazzDesc, methodBaseName, methodDesc};
		Query query = null;
		try
		{
			query = MultiFieldQueryParser.parse(Configuration.LUCENE_VERSION, termVector, columnVector, clauseVector, analyser);
			query = updateBoost(query);
			System.out.println("aa1");
		}
		catch(Exception e)
		{
			System.out.println(e);
			//TODO  Fix for empty search Str
		}
		return query;
		
	}
	
	private static Query updateBoost(Query query) throws Exception {
		Map<String, Map<String, Double>> boostMap = searcherBoost(query,null);
		if(query instanceof BooleanQuery)
		{
			BooleanQuery boolQuery = (BooleanQuery)query;
			for(BooleanClause clause: boolQuery.clauses())
			{
				bootFields(boostMap, clause,Configuration.IDX_FIELD_DESCRIPTION);
				bootFields(boostMap, clause,Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT);
				//bootFields(boostMap, clause,Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT);
				
			}
		}
		return query;
	}

	private static void bootFields(Map<String, Map<String, Double>> boostMap,BooleanClause clause, String field) {
		if(clause.getQuery().toString().startsWith(field +":") ||
				clause.getQuery().toString().startsWith("["+field+":"))
		{
			//clause.getQuery().setBoost(Float.valueOf("1.5"));
			
			if(clause.getQuery() instanceof BooleanQuery)
			{
				for(BooleanClause clss: ((BooleanQuery)clause.getQuery()).clauses())
				{
					if(clss.getQuery() instanceof TermQuery)
					{
						TermQuery tq = (TermQuery) clss.getQuery();
						System.err.println(tq.getTerm().text());
						if(boostMap.get(field).containsKey(tq.getTerm().text()))
							tq.setBoost(boostMap.get(field).get(tq.getTerm().text()).floatValue());
					}
					System.err.println(clss.toString());
				}
			}
		}
	}

	private static String getJavaMethodName(String name) {
		try{
		name = name.substring(0,name.indexOf("("));
		name = name.substring(name.lastIndexOf(" ")).trim();
		return name;
		}catch(Exception e)
		{
			
		}
		return null;
	}

	private static String getDescriptionQuery(String name, String descBlob) {
		name = name==null?"a":name;
		descBlob = descBlob==null?"a":descBlob;
		descBlob = Jsoup.parse(descBlob).text();
		String desclist[] = descBlob.split("\\.");
		String description = "a";
		description = desclist[0]==null?"a":desclist[0];
		
		if(desclist.length>1 && desclist[1]!=null)
			description = description  + ". " + desclist[1];
		//empty Descriptions
		description = description.trim().equals("")?StringUtil.splitCamelCase(name):description;
		description = description.trim().equals("")?"a":description;
		return description;
	}
	
	private static String getClazzDescriptionQuery(String name, String descBlob) {
		name = name==null?"a":name;
		descBlob = descBlob==null?"a":descBlob;
		descBlob = Jsoup.parse(descBlob).text();
		String desclist[] = descBlob.split("\\.");
		String description = "a";
		description = desclist[0]==null?"a":desclist[0];
		
		if(desclist.length>1 && desclist[1]!=null)
			description = description  + ". " + desclist[1];
		
		if(desclist.length>2 && desclist[2]!=null)
			description = description  + ". " + desclist[1];
		
		if(desclist.length>3 && desclist[3]!=null)
			description = description  + ". " + desclist[1];
		
		if(desclist.length>4 && desclist[4]!=null)
			description = description  + ". " + desclist[1];
		//empty Descriptions
		description = description.trim().equals("")?StringUtil.splitCamelCase(name):description;
		description = description.trim().equals("")?"a":description;
		return description;
	}
	
	
	
	public static List<SearchResultBean> executeQuery(String query) throws Exception
	{
		QueryParser queryParser = new QueryParser(Configuration.LUCENE_VERSION, Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL, analyser);
		return executeQuery(queryParser.parse(query));
	}
	
	public static List<SearchResultBean> executeQuery(Query query) throws Exception
	{	
		
		
		List<SearchResultBean> displayList = new ArrayList<SearchResultBean>();
		if(query instanceof BooleanQuery)
		{
			BooleanQuery boolQuery = (BooleanQuery)query;
			for(BooleanClause clause: boolQuery.clauses())
			{
				if(clause.getQuery().toString().startsWith(Configuration.IDX_FIELD_DESCRIPTION+":") ||
						clause.getQuery().toString().startsWith("["+Configuration.IDX_FIELD_DESCRIPTION+":"))
					clause.getQuery().setBoost(Float.valueOf("1.5"));
				
			}
		}	
			List<Document> result = searcher(query, null);
			
			
			for(Document doc:result)
			{
				SearchResultBean bean = new SearchResultBean();
				bean.setClsName(doc.get(Configuration.IDX_FIELD_CLASS_BASE_NAME));
				bean.setClsNameCCSplit(doc.get(Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT));
				bean.setMtdDesc(doc.get(Configuration.IDX_FIELD_DESCRIPTION));
				bean.setMtdName(doc.get(Configuration.IDX_FIELD_METHOD_NAME));
				bean.setMtdNameCCSplit(doc.get(Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT));
				bean.setPkgName(doc.get(Configuration.IDX_FIELD_PKG_NAME));
				bean.setScore(doc.get("SCORE"));
				displayList.add(bean);
			}
		return displayList;
	}
	
	
	
	private static Map<String, Map<String, Double>> searcherBoost(Query query, Filter filter) throws Exception {
		Directory index = FSDirectory.open(new File(idxLocSrc));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
		if(filter!=null)
			searcher.search(query, collector);
		else
			searcher.search(query, filter, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		Terms t;
		String fieldName1 = Configuration.IDX_FIELD_DESCRIPTION;
		String fieldName2 = Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT;
		String fieldName3 = Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT;
		Map<String, Map<String,Double>> boostMap = new HashMap<String, Map<String,Double>>(); 
		for (int i = 0; i < hits.length; ++i) 
		{
			int docId = hits[i].doc;
			if(i==0)
			{
				t = reader.getTermVector(docId, fieldName1+"_VEC");
				boostMap.put(fieldName1, mtd(t, docId, reader, fieldName1+"_VEC"));
				t = reader.getTermVector(docId, fieldName2+"_VEC");
				boostMap.put(fieldName2, mtd(t, docId, reader, fieldName2+"_VEC"));
				t = reader.getTermVector(docId, fieldName3+"_VEC");
				boostMap.put(fieldName3, mtd(t, docId, reader, fieldName3+"_VEC"));
			}
			
			
		}
		return boostMap;
	}
	
	private static List<Document> searcher(Query query, Filter filter) throws Exception {
		// System.err.println("here");
		List<Document> retList = new ArrayList<>();
		Directory index = FSDirectory.open(new File(idxLoc));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		if(filter!=null)
			searcher.search(query, collector);
		else
			searcher.search(query, filter, collector);
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
	
	private static Map<String, Double> mtd(Terms vector, int docID, IndexReader reader, String fieldName) throws Exception {
		TermsEnum termsEnum = vector.iterator(null);
		BytesRef bytesRef = null;
		DocsEnum docsEnum = null;
		TreeSet<IDSorter<String>> returnSet = new TreeSet<IDSorter<String>>(
				new Comparator<IDSorter<String>>() {
					@Override
					public int compare(IDSorter<String> o1, IDSorter<String> o2) {
						if (o1.getRank() < o2.getRank())
							return 1;
						return -1;
					}
				});
		Map<String, Pair<Double, Double>> idfMap = new HashMap<String, Pair<Double,Double>>();
		double max_freq=0, idf=0, tf=0;
		Pair<Double, Double> tfIdfValuePair;
		while ((bytesRef = termsEnum.next()) != null) {
			tfIdfValuePair = new Pair<Double, Double>();
			String term = bytesRef.utf8ToString();
			tf = 0;
			docsEnum = termsEnum.docs(null, null, DocsEnum.FLAG_FREQS);
			docsEnum.nextDoc();
			tf = docsEnum.freq();
			if(tf>max_freq)
				max_freq = tf;
			idf = reader.docFreq(new Term(fieldName, termsEnum.term()));
			
			tfIdfValuePair.setFirst(tf);
			tfIdfValuePair.setSecond(Math.log(1 + (reader.numDocs() / idf)));
			
			idfMap.put(term, tfIdfValuePair);
			
		}
		double weight;
		for(String term: idfMap.keySet())
		{
			tfIdfValuePair = idfMap.get(term);
			weight = (0.5 + (0.5 * tfIdfValuePair.getFirst())/max_freq) * tfIdfValuePair.getSecond();
			returnSet.add(new IDSorter<String>(term, weight));
		}
		double range = returnSet.first().getRank()-returnSet.last().getRank();
		double baseMin = returnSet.last().getRank();
		double val;
		
		Map<String, Double> tfidfMap = new HashMap<String, Double>();
		for (IDSorter<String> ss : returnSet)
		{
			val = 0 + (1*(ss.getRank()-baseMin)/range);
			val = Math.round(val*10);
			val = val/10;
			if(val>=0.5)
			{	
				tfidfMap.put(ss.getDoc(), val);
				System.err.println(ss.getDoc()+":"+val);
			}
		}
		
		
		return tfidfMap;
	}	
}