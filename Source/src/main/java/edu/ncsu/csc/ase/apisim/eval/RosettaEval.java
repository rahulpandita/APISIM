package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.eval.oracle.RosettaOracle;
import edu.ncsu.csc.ase.apisim.eval.util.CustomComparator;
import edu.ncsu.csc.ase.apisim.eval.util.ResultRep;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

public class RosettaEval{

	private Map<String, List<String>> baseResult;
	
	private Map<String, List<String>> mtdMap;
	
	private List<String> subClassList;
	
	private Analyzer analyser;

	private String idxLoc = "";
	
	private int hitsPerPage = 30;
	
 	public int getHitsPerPage() {
		return hitsPerPage;
	}

	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}

	private List<APIType> clazzList ;
	
	public RosettaEval(List<APIType> clazzList, String idxFileLoc) {
		initialize(idxFileLoc, new EnglishAnalyzer(Configuration.LUCENE_VERSION), clazzList);
	}

	public RosettaEval(List<APIType> clazzList, String idxFileLoc, Analyzer analyzer) {
		initialize(idxFileLoc, analyzer, clazzList);
	}
	
	private void initialize(String idxFileLoc, Analyzer analyzer, List<APIType> clazzList) {
		this.analyser = analyzer;
		this.idxLoc = idxFileLoc;
		this.clazzList = clazzList;
		this.baseResult = new HashMap<String, List<String>>();
		this.mtdMap = new HashMap<String, List<String>>();
		try
		{
			baseResult = RosettaOracle.read();
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		subClassList = new ArrayList<String>();
		String srcClazz,srcMtd;
		for(String srcStr: baseResult.keySet())
		{
			srcClazz = srcStr.split("_")[0];
			srcMtd = srcStr.split("_")[1];
			List<String> mtdList;
			if(!subClassList.contains(srcClazz))
			{
				subClassList.add(srcClazz);
				mtdList = new ArrayList<String>();
				mtdList.add(srcMtd);
				mtdMap.put(srcClazz, mtdList);
			}
			else
			{
				mtdList = mtdMap.get(srcClazz);
				if(!mtdList.contains(srcMtd))
					mtdList.add(srcMtd);
			}
			mtdMap.put(srcClazz, mtdList);
		}
	}
	
	public void evalWrapper() throws Exception
	{
		Map<String, List<String>> finalMap = new HashMap<String, List<String>>();
		for(int i = 10;i<50;i=i+5){
			hitsPerPage = i;	
			Map<String, Map<APIMtd, List<Document>>> resultMap = eval();
			Map<APIMtd, List<Document>> mtdMap;
			String key,key1;
			List<String> resultList;
			List<String> docList;
			for(String clazzName: resultMap.keySet())
			{
				mtdMap = resultMap.get(clazzName);
				for(APIMtd mtd: mtdMap.keySet())
				{
					key = clazzName+ "_" + getMethodbaseName(mtd);
					key1 = clazzName + "_" + mtd.getName();
					resultList =  baseResult.get(key);
					docList = decorate(mtdMap.get(mtd));
					List<String> rankList;
					if(finalMap.containsKey(key1))
					{
						rankList = finalMap.get(key1);
					}
					else
					{
						rankList = new ArrayList<String>();
					}
					rankList.add(""+getRank(resultList, docList));
					finalMap.put(key1, rankList);
					
				}
				
			}
		}
		writeDataToExcel(finalMap, "abc.xlsx");
	}
	
	private int getRank(List<String> resultList, List<String> docList) {
		int rank = Integer.MAX_VALUE;
		
		for(String rosetaString: resultList)
		{
			for(int i=0; i<docList.size();i++)
			{
				if(rosetaString.contains(docList.get(i)))
				{
					if(rank > i)
					{
						rank = i;
						if(i==0)
							break;
					}
				}
			}
		}
		
		return rank;
	}

	private List<String> decorate(List<Document> list) {
		List<String> retList = new ArrayList<String>();
		for(Document doc:list)
		{
			retList.add(doc.get(Configuration.IDX_FIELD_CLASS_NAME)+"_"+doc.get(Configuration.IDX_FIELD_METHOD_BASE_NAME));
		}
		return retList;
	}

	public Map<String, Map<APIMtd, List<Document>>> eval() throws Exception 
	{
		Map<String, Map<APIMtd, List<Document>>> resultMap = new HashMap<String, Map<APIMtd, List<Document>>>();
		Map<String, List<APIMtd>> includedMap = inclusionList();
		for (String page : includedMap.keySet()) 
		{
			List<APIMtd> argList = includedMap.get(page);
			Map<APIMtd, List<Document>> objResMap = new HashMap<APIMtd, List<Document>>();
			for (APIMtd mtd : argList) 
			{
				List<Query> queryList = createQueryList(getClauseVector(), getTermVector(mtd), getColumnVector());
				List<Document> docList = customRanking(queryList);
				objResMap.put(mtd, docList);
			}
			resultMap.put(page, objResMap);
		}

		return resultMap;
	}
	
	private Map<String, List<APIMtd>> inclusionList() {
		Map<String, List<APIMtd>> returnMap = new HashMap<String, List<APIMtd>>();
		for(APIType clazz: clazzList)
		{
			if(inclusionCriteriaClass(clazz))
			{
				List<APIMtd> mtdList = new ArrayList<APIMtd>();
				for(APIMtd mtd:clazz.getMethod())
				{
					if(inclusionCriteriaMethod(clazz.getPackage().trim() + "." + clazz.getName().trim(), getMethodbaseName(mtd)))
					{
						mtdList.add(mtd);
					}
				}
				returnMap.put(clazz.getPackage().trim() + "." + clazz.getName().trim(), mtdList);
			}
		}
		return returnMap;
	}
	
	private boolean inclusionCriteriaClass(APIType clazz) {
		return subClassList.contains(clazz.getPackage().trim() + "." + clazz.getName().trim());
	}

	private boolean inclusionCriteriaMethod(String clazz, String mtd) {
		List<String> mtdList = mtdMap.get(clazz);
		if(mtdList.contains(mtd))
			return true;
		return false;
	}

	private List<Document> searcher(Query query) throws Exception {
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
			retList.add(d);
		}
		return retList;
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
	
	private List<Query> createQueryList(List<Occur[]> clauseVecList, List<String[]> termVecList, List<String[]> columnVecList) throws ParseException {
		if ((clauseVecList.size() != termVecList.size()) && (clauseVecList.size() != columnVecList.size()))
			throw new IllegalArgumentException("Clause, Term, and Column Vector Lists are not of equal size");
		
		List<Query> queryList = new ArrayList<Query>();
		
		for (int i = 0; i < clauseVecList.size(); i++) 
		{
			if ((clauseVecList.get(i).length != termVecList.get(i).length) && (clauseVecList.get(i).length != columnVecList.get(i).length))
				throw new IllegalArgumentException("Clause, Term, and Column Vectors are not of equal size at index - "	+ i);
			queryList.add(createQuery(clauseVecList.get(i), termVecList.get(i),	columnVecList.get(i)));
		}
		return queryList;
	}
	
	private Query createQuery(Occur[] clauseVector, String[] termVector, String[] columnVector) throws ParseException {
		Query query = null;
		try
		{
			query = MultiFieldQueryParser.parse(Configuration.LUCENE_VERSION, termVector, columnVector, clauseVector, analyser);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return query;
	}
	
	
	private String getMethodDescription(APIMtd mtd) {
		String desclist[] = mtd.getDescription().split("\\.");
		String methodDesc = "a";
		methodDesc = desclist[0]==null?"a":desclist[0];
		return methodDesc;
	}

	public List<Document> customRanking(List<Query> queryList) throws Exception {
		List<Document> result = new ArrayList<Document>();
		Map<String , ResultRep> docMap = new LinkedHashMap<String, ResultRep>();
		
		try
		{
			Float avg = new Float(queryList.size());
			Float rank;
			for(Query query: queryList)
			{
				System.out.println(query);
				result = searcher(query);
				for(int i= 0; i<result.size();i++)
				{
					Document doc = result.get(i);
					String key = doc.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL);
					rank = new Float(result.size()-i);
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
			result.add(rep.getDoc());
		}
		
		//if(result.size()>10)
		//	result = result.subList(0, 10);
		return result;
	}

	public List<Occur[]> getClauseVector() {
		List<Occur[]> clauseList = new ArrayList<Occur[]>();
		Occur[] occur1 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur2 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur3 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur4 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};

		clauseList.add(occur1);
		clauseList.add(occur2);
		clauseList.add(occur3);
		clauseList.add(occur4);
		
		return clauseList;
	}

	public List<String[]> getColumnVector() {
		List<String[]> columnList = new ArrayList<String[]>();
		String[] columnVec = new String[] {
				Configuration.IDX_FIELD_API_NAME,
				Configuration.IDX_FIELD_CLASS_NAME_PKG_SPLIT,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL};
		
		columnList.add(columnVec);
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		
		return columnList;
	}

	public List<String[]> getTermVector(APIMtd mtd) {
		List<String[]> termList = new ArrayList<String[]>();
		String apiName = MultiFieldQueryParser.escape(APITYPE.ANDROID.name().toLowerCase());
		String clazzName = MultiFieldQueryParser.escape(mtd.getParentClass().getName());
		String methodBaseName = MultiFieldQueryParser.escape(getMethodbaseName(mtd));
		String methodDesc = MultiFieldQueryParser.escape(getMethodDescription(mtd));
		
		String[] termVec1 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec2 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		String[] termVec3 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec4 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		
		termList.add(termVec1);
		termList.add(termVec2);
		termList.add(termVec3);
		termList.add(termVec4);
		
		return termList;
	}
	
	public void writeDataToExcel(Map<String, List<String>> finalMap, String fileName) throws Exception 
	{
		File f = new File(fileName);
		if(f.exists())
		{
			System.out.println("Old Results File Found");
			FileUtils.deleteQuietly(f);
			System.out.println("Old Results File Deleted");
			
		}
		
		
		SXSSFWorkbook myWorkBook = new SXSSFWorkbook();
		Sheet mySheet = myWorkBook.createSheet("A");
		Row myRow = null;
		int i=0;
		for(String clazz: finalMap.keySet())
		{
			List<String> result = finalMap.get(clazz);
			myRow = mySheet.createRow(i);
			myRow.createCell(0).setCellValue(clazz);
			for(int j=0; j<result.size();j++)
			{
				myRow.createCell(j+1).setCellValue(result.get(j));
			}
			i++;
		}
		FileOutputStream out = new FileOutputStream(fileName);
		myWorkBook.write(out);
		out.close();
		
	}
}
