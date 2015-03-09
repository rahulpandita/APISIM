package edu.ncsu.csc.ase.apisim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.queryparser.surround.parser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.eval.util.CustomComparator;
import edu.ncsu.csc.ase.apisim.eval.util.ResultRep;
import edu.ncsu.csc.ase.apisim.topicModel.TMResults;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class EvalJava extends PremEval<APIMtd> 
{
	
	
	
	private List<APIType> clazzList ;
	
	//public static HashMap<String, Set<String>> topicClassMap = new HashMap<String, Set<String>>();
	
	public static String pkgStr;
	
	private static List<String> inclusionList;
	
	public EvalJava(List<APIType> clazzList, String idxFileLoc, String pkg) 
	{
		super(idxFileLoc);
		initialize(clazzList);
		pkgStr = pkg;
	}
	
	public EvalJava(List<APIType> clazzList, String idxFileLoc, Analyzer analyzer,  String pkg) 
	{
		super(idxFileLoc, analyzer);
		initialize(clazzList);
		pkgStr = pkg;
	}


	private void initialize(List<APIType> clazzList) {
		this.clazzList = clazzList;
		
		this.hitsPerPage = 100;
		
		try {
			Map<String, List<String>> mm = TMResults.readPkg(TMResults.pkgClsMtdSum);
			List<String> lst;
			
			for(String key:mm.keySet())
			{
				lst = mm.get(key);
				if(lst.size()>50)
					lst = lst.subList(0, 50);
				//topicClassMap.put(key, new TreeSet<String>(lst));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inclusionList = new ArrayList<String>();
		inclusionList.add("java.util.concurrent.atomic.AtomicBoolean");
		inclusionList.add("java.util.jar.JarInputStream");
		inclusionList.add("java.util.jar.Attributes");
		inclusionList.add("java.util.jar.Manifest");
		inclusionList.add("java.util.LinkedHashMap");
		inclusionList.add("java.util.AbstractCollection");
		inclusionList.add("java.util.Date");
		inclusionList.add("java.util.HashSet");
		inclusionList.add("java.util.SortedSet");
		inclusionList.add("java.util.Set");
		inclusionList.add("java.util.TreeSet");
		inclusionList.add("java.util.BitSet");
		inclusionList.add("java.util.Hashtable");
		inclusionList.add("java.util.TreeMap");
		inclusionList.add("java.util.SortedMap");
		inclusionList.add("java.util.Iterator");
		inclusionList.add("java.util.IdentityHashMap");
		inclusionList.add("java.util.Vector");
		inclusionList.add("java.util.WeakHashMap");
		inclusionList.add("java.util.StringTokenizer");
		inclusionList.add("java.util.EventObject");
		inclusionList.add("java.util.Locale");
		inclusionList.add("java.util.Map.Entry");
		inclusionList.add("java.util.UUID");
		inclusionList.add("java.util.EventListener");
		inclusionList.add("java.util.AbstractList");
		inclusionList.add("java.util.TimerTask");
		inclusionList.add("java.util.Timer");
		inclusionList.add("java.util.EmptyStackException");
		inclusionList.add("java.util.Collections");
		inclusionList.add("java.util.ListIterator");
		inclusionList.add("java.util.HashMap");
		inclusionList.add("java.util.Calendar");
		inclusionList.add("java.util.ResourceBundle");
		inclusionList.add("java.util.Properties");
		inclusionList.add("java.util.Stack");
		inclusionList.add("java.util.MissingResourceException");
		inclusionList.add("java.util.Comparator");
		inclusionList.add("java.util.ArrayList");
		inclusionList.add("java.util.Collection");
		inclusionList.add("java.util.Map");
		inclusionList.add("java.util.ConcurrentModificationException");
		inclusionList.add("java.util.Arrays");
		inclusionList.add("java.util.Currency");
		inclusionList.add("java.util.NoSuchElementException");
		inclusionList.add("java.util.Enumeration");
		inclusionList.add("java.util.LinkedList");
		inclusionList.add("java.util.GregorianCalendar");
		inclusionList.add("java.util.Random");
		inclusionList.add("java.util.PropertyResourceBundle");
		inclusionList.add("java.util.List");
		inclusionList.add("java.util.regex.Pattern");
		inclusionList.add("java.util.regex.Matcher");
		
	}
	
	public boolean inclusionCriteriaMethod(APIMtd mtd) {
		return true;
	}

	public boolean inclusionCriteriaClass(APIType clazz) {
		
		return inclusionList.contains(clazz.getPackage()+"."+clazz.getName());
	}

	private String getMethodbaseName(APIMtd mtd) 
	{
		
		return mtd.getName();
	}
	
	private String getMethodDescription(APIMtd mtd) {
		String desclist[] = mtd.getDescription().split("\\.");
		String methodDesc = "a";
		methodDesc = desclist[0]==null?"a":desclist[0];
		
		if(desclist.length>1 && desclist[1]!=null)
			methodDesc = methodDesc  + ". " + desclist[1];
		//empty Descriptions
		methodDesc = methodDesc.trim().equals("")?StringUtil.splitCamelCase(mtd.getName()):methodDesc;
		methodDesc = methodDesc.trim().equals("")?"a":methodDesc;
		return methodDesc;
	}

	@Override
	public List<Document> customRanking(List<Query> queryList) throws Exception {
		
		List<Document> result = new ArrayList<Document>();
		Map<String , ResultRep> docMap = new LinkedHashMap<String, ResultRep>();
		BooleanQuery bq = new BooleanQuery();
		
		bq.setMinimumNumberShouldMatch(1);
		
		Filter idFilter = new CachingWrapperFilter( new QueryWrapperFilter(bq));
		
		try
		{
			Float avg = new Float(queryList.size());
			Float similarity, baseSimilarity, srcScoreRange, targetScoreRange;
			targetScoreRange = new Float(1);
			for(int idx =0; idx < queryList.size() ; idx++)
			{
				Query query = queryList.get(idx);
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
				result = searcher(query,idFilter);
				baseSimilarity = Float.MIN_VALUE;
				srcScoreRange = Float.MIN_VALUE;
				if(result.size()>0)
				{
					baseSimilarity = Float.parseFloat(result.get(result.size()-1).get("SCORE"));
					srcScoreRange = Float.parseFloat(result.get(0).get("SCORE")) - Float.parseFloat(result.get(result.size()-1).get("SCORE"));
				}
				for(int i= 0; i<result.size();i++)
				{
					Document doc = result.get(i);
					String key = doc.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL);
					similarity = Float.parseFloat(doc.get("SCORE"))-baseSimilarity;
					similarity = similarity*(targetScoreRange/srcScoreRange);
					similarity = similarity/avg;
					similarity = similarity * (Float.parseFloat("0.01")*(avg-idx));
					if(!docMap.containsKey(key))
					{
						docMap.put(key, new ResultRep(doc, similarity));
					}
					else
					{
						ResultRep rep = docMap.get(key);
						rep.setRank(rep.getRank() + similarity);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		
		TreeSet<ResultRep> set = new TreeSet<ResultRep>(new CustomComparator());
		
		for(String key: docMap.keySet())
			set.add(docMap.get(key));
		
		result = new ArrayList<Document>();
		Iterator<ResultRep> iter = set.descendingIterator();
		ResultRep rep;
		Document doc;
		while(iter.hasNext())
		{
			rep = iter.next();
			doc = rep.getDoc();
			//Update field Score
			doc.removeField("SCORE");
			doc.add(new FloatField("SCORE", rep.getRank(), Field.Store.NO) );
			
			result.add(doc);
		}
		
		
		if(result.size()>10)
			result = result.subList(0, 10);
		return result;
	}

	@Override
	public List<Occur[]> getClauseVector() 
	{
		List<Occur[]> clauseList = new ArrayList<Occur[]>();
		Occur[] occur1 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur2 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur3 = {Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur4 = {Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur5 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		
		clauseList.add(occur1);
		clauseList.add(occur2);
		clauseList.add(occur3);
		clauseList.add(occur4);
		clauseList.add(occur5);
		return clauseList;
	}

	@Override
	public List<String[]> getColumnVector() {
		List<String[]> columnList = new ArrayList<String[]>();
		String[] columnVec = new String[] {
				
				Configuration.IDX_FIELD_CLASS_BASE_NAME,
				Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT,
				Configuration.IDX_FIELD_DESCRIPTION};
		String[] columnVec1 = new String[] {
				
				Configuration.IDX_FIELD_CLASS_NAME_PKG_SPLIT,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_DESCRIPTION};
		
		columnList.add(columnVec);
		columnList.add(Arrays.copyOf(columnVec, 3));
		columnList.add(Arrays.copyOf(columnVec, 3));
		columnList.add(Arrays.copyOf(columnVec, 3));
		columnList.add(columnVec1);
		return columnList;
	}
	//private Set<String> tstSet;
	

	@Override
	public List<String[]> getTermVector(APIMtd mtd) {
		
		List<String[]> termList = new ArrayList<String[]>();
		
		String clazzName = MultiFieldQueryParser.escape(mtd.getParentClass().getName());
		String methodBaseName = MultiFieldQueryParser.escape(getMethodbaseName(mtd));
		String methodDesc = MultiFieldQueryParser.escape(getMethodDescription(mtd));
		
		try {
			String[] fieldVec = new String[]{Configuration.IDX_FIELD_CLASS_BASE_NAME};
			String[] termVec = new String[]{clazzName};
			Query query = QueryParserUtil.parse(termVec,fieldVec,analyser);
			clazzName = query.toString().replaceAll(Pattern.quote(Configuration.IDX_FIELD_CLASS_BASE_NAME+":"), "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] termVec1 = new String[] {clazzName, methodBaseName, methodDesc};
		String[] termVec2 = new String[] {clazzName+"*", methodBaseName, methodDesc};
		String[] termVec3 = new String[] {clazzName, methodBaseName, methodDesc};
		String[] termVec4 = new String[] {clazzName+"*", methodBaseName, methodDesc};
		String[] termVec5 = new String[] {clazzName+"*", methodBaseName, methodDesc};
		
		termList.add(termVec1);
		termList.add(termVec2);
		termList.add(termVec3);
		termList.add(termVec4);
		termList.add(termVec5);
		
		return termList;
	}

	@Override
	public Map<String, List<APIMtd>> inclusionList() {
		Map<String, List<APIMtd>> returnMap = new HashMap<String, List<APIMtd>>();
		for(APIType clazz: clazzList)
		{
			if(inclusionCriteriaClass(clazz))
			{
				List<APIMtd> mtdList = new ArrayList<APIMtd>();
				for(APIMtd mtd:clazz.getMethod())
				{
					if(inclusionCriteriaMethod(mtd))
					{
						mtdList.add(mtd);
					}
				}
				returnMap.put(clazz.getName(), mtdList);
			}
		}
		return returnMap;
	}
	
}
