package edu.ncsu.csc.ase.apisim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.eval.oracle.TpMdlClsCons;
import edu.ncsu.csc.ase.apisim.eval.util.CustomComparator;
import edu.ncsu.csc.ase.apisim.eval.util.ResultRep;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class Eval4 extends PremEval<APIMtd> 
{
	
	private List<String> subClassList;
	
	private List<APIType> clazzList ;
	
	public Eval4(List<APIType> clazzList, String idxFileLoc) 
	{
		super(idxFileLoc);
		initialize(clazzList);
	}
	
	public Eval4(List<APIType> clazzList, String idxFileLoc, Analyzer analyzer) 
	{
		super(idxFileLoc, analyzer);
		initialize(clazzList);
	}


	private void initialize(List<APIType> clazzList) {
		this.clazzList = clazzList;
		this.subClassList = new ArrayList<String>();
		this.hitsPerPage = 200;
		subClassList.add("Alert");
		subClassList.add("Command");
		subClassList.add("Font");
		subClassList.add("Displayable");
		subClassList.add("Display");
		subClassList.add("Canvas");
		subClassList.add("GameCanvas");
		subClassList.add("Layer");
		subClassList.add("Sprite");
		subClassList.add("Graphics");
		subClassList.add("Image");
	}
	
	public boolean inclusionCriteriaMethod(APIMtd mtd) {
		return true;
	}

	public boolean inclusionCriteriaClass(APIType clazz) {
		return subClassList.contains(clazz.getName().trim());
	}

	private String getMethodbaseName(APIMtd mtd) 
	{
		String name = ASTBuilder.getJavaMethodName(mtd.getName());
		if(name==null||name.length()==0)
		{
			String methodNameTrunc = mtd.getName().substring(0, mtd.getName().indexOf('(')).trim();
			String[] methodTokens = methodNameTrunc.split("\\s+");
			String methodBaseName = mtd.getName();
			if(methodTokens.length >= 2) 
				methodBaseName = methodTokens[methodTokens.length-1];
			else
				methodBaseName = methodNameTrunc;
			return methodBaseName;
		}
		return name;
	}
	
	private String getMethodDescription(APIMtd mtd) {
		String desclist[] = mtd.getDescription().split("\\.");
		String methodDesc = "a";
		methodDesc = desclist[0]==null?"a":desclist[0];
		return methodDesc;
	}

	@Override
	public List<Document> customRanking(List<Query> queryList) throws Exception {
		
		List<Document> result = new ArrayList<Document>();
		Map<String , ResultRep> docMap = new LinkedHashMap<String, ResultRep>();
		BooleanQuery bq = new BooleanQuery();
		
		bq.setMinimumNumberShouldMatch(1);
		Set<String> tgtset = tstSet==null?new HashSet<String>():tstSet; 
		for(String clsStr:tgtset)
			bq.add(new TermQuery(new Term(Configuration.IDX_FIELD_CLASS_NAME, QueryParser.escape(clsStr))), Occur.SHOULD);
		Filter idFilter = new QueryWrapperFilter(bq);
		
		try
		{
			Float avg = new Float(queryList.size());
			Float rank;
			//BooleanQuery mainQuery;
			for(Query query: queryList)
			{
				//mainQuery = new BooleanQuery();
				//mainQuery.add(query, Occur.SHOULD);
				//mainQuery.add(idFilter, Occur.MUST);
				result = searcher(query,idFilter);
				for(int i= 0; i<result.size();i++)
				{
					Document doc = result.get(i);
					String key = doc.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL);
					rank = Float.parseFloat(doc.get("SCORE"));
					rank = rank/avg;
					if(!docMap.containsKey(key))
						docMap.put(key, new ResultRep(doc, rank));
					else
					{
						ResultRep rep = docMap.get(key);
						rep.setRank(rep.getRank() + rank);
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
		while(iter.hasNext())
		{
			rep = iter.next();
			if(tgtset.contains(rep.getDoc().get(Configuration.IDX_FIELD_CLASS_NAME).trim()))
				result.add(rep.getDoc());
		}
		
		
		if(result.size()>10)
			result = result.subList(0, 10);
		return result;
	}

	@Override
	public List<Occur[]> getClauseVector() 
	{
		List<Occur[]> clauseList = new ArrayList<Occur[]>();
		Occur[] occur1 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur2 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur3 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur4 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur5 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		
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
				Configuration.IDX_FIELD_API_NAME,
				Configuration.IDX_FIELD_CLASS_BASE_NAME,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_DESCRIPTION};
		String[] columnVec1 = new String[] {
				Configuration.IDX_FIELD_API_NAME,
				Configuration.IDX_FIELD_CLASS_NAME_PKG_SPLIT,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_DESCRIPTION};
		
		columnList.add(columnVec);
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(columnVec1);
		return columnList;
	}
	private Set<String> tstSet;
	

	@Override
	public List<String[]> getTermVector(APIMtd mtd) {
		tstSet = TpMdlClsCons.topicClassMap.get(mtd.getParentClass().getPackage().trim()+"."+mtd.getParentClass().getName().trim());
		List<String[]> termList = new ArrayList<String[]>();
		String apiName = MultiFieldQueryParser.escape(APITYPE.ANDROID.name().toLowerCase());
		String clazzName = MultiFieldQueryParser.escape(mtd.getParentClass().getName());
		String methodBaseName = MultiFieldQueryParser.escape(getMethodbaseName(mtd));
		String methodDesc = MultiFieldQueryParser.escape(getMethodDescription(mtd));
		
		String[] termVec1 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec2 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		String[] termVec3 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec4 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		String[] termVec5 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		
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
