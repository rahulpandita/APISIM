package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.util.FileUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class PremEval 
{
	private static Analyzer ANALYZER = new EnglishAnalyzer(Configuration.LUCENE_VERSION);
	private static String EVAL_FOLDER = "";
	private static String ANA_ENG = "ENGANA";
	private static String ANA_SYN = "SYNANA";
	private static String IDX_ENG = "SIMPLEIDX";
	private static String IDX_SYN = "SYNIDX";
	private static String RESULT_BASE = "premResults";
	private static String IDX_LOCATION = ""; 
	private List<String> subClassList;
	private List<APIType> clazzList ;
	private int evalType = 0;
	
	public PremEval(int evalType, List<APIType> clazzList) 
	{
		this.subClassList = new ArrayList<String>();
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
		
		this.clazzList = clazzList;
		this.evalType = evalType;
	}
	
	
	private Query createQuery1(String clsName, String mtdBaseName, String mtdDesc) throws ParseException
	{
		return createQueryBase(Occur.MUST, false, clsName, mtdBaseName, mtdDesc);
	}
	
	private Query createQuery2(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.MUST, true, className, methodBaseName, methodDesc);
	}
	
	private Query createQuery3(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.SHOULD, false, className, methodBaseName, methodDesc);
	}
	
	private Query createQuery4(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.SHOULD, true, className, methodBaseName, methodDesc);
	}
	
	
	private Query createQueryBase(Occur clause, Boolean wildCard, String clsName, String mtdBaseName, String mtddDesc) throws ParseException
	{
		mtdBaseName = MultiFieldQueryParser.escape(mtdBaseName.toLowerCase());
		mtddDesc = MultiFieldQueryParser.escape(mtddDesc.toLowerCase());
		clsName = MultiFieldQueryParser.escape(clsName.toLowerCase());
		clsName = wildCard?clsName+"*":clsName;
		/*
	 		+APINAME: android
			+CLASSNAME1:canvas
			METHODNAME:getHeight
			MTDDESCRIPTION:Gets height of the displayable area in pixels.
		 */
		BooleanClause.Occur bc1 = BooleanClause.Occur.MUST;
		BooleanClause.Occur bc2 = clause;
		BooleanClause.Occur bc3 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur bc4 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur[] flags = { bc1, bc2, bc3, bc4 };
		
		String[] termVector = new String[] {"android", clsName, mtdBaseName, mtddDesc};
		String[] columnVector = new String[] {"APINAME","CLASSNAME1","METHODNAME","MTDDESCRIPTION"};
		
		Query query = MultiFieldQueryParser.parse(Configuration.LUCENE_VERSION, termVector, columnVector, flags, ANALYZER);
		
		return query;
	}
	
	/**
	 * test method to be removed from production code
	 * @param args
	 * @throws Exception 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception {
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		PremEval.EVAL_FOLDER = PremEval.RESULT_BASE + File.separator+ PremEval.IDX_ENG + File.separator + PremEval.ANA_ENG + File.separator;
		PremEval.IDX_LOCATION = Configuration.API_IDX_FILE;
		
		
		PremEval eval = new PremEval(1, clazzList);
		eval.eval();
		
		PremEval.EVAL_FOLDER = PremEval.RESULT_BASE + File.separator+ PremEval.IDX_SYN + File.separator + PremEval.ANA_ENG + File.separator;
		PremEval.IDX_LOCATION = Configuration.API_IDX_FILE_SYNONYM;
		eval = new PremEval(1, clazzList);
		eval.eval();
		
		PremEval.ANALYZER = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		
		PremEval.EVAL_FOLDER = PremEval.RESULT_BASE + File.separator+ PremEval.IDX_ENG + File.separator + PremEval.ANA_SYN + File.separator;
		PremEval.IDX_LOCATION = Configuration.API_IDX_FILE;
		eval = new PremEval(1, clazzList);
		eval.eval();
		
		PremEval.EVAL_FOLDER = PremEval.RESULT_BASE + File.separator+ PremEval.IDX_SYN + File.separator + PremEval.ANA_SYN + File.separator;
		PremEval.IDX_LOCATION = Configuration.API_IDX_FILE_SYNONYM;
		eval = new PremEval(1, clazzList);
		eval.eval();
		
		
	}
	
	
	public void eval() throws Exception 
	{
		
		StringBuffer buff = new StringBuffer();
		//Graphics
		for (APIType clazz : clazzList) 
		{
			if(subClassList.contains(clazz.getName().trim()))
			{
				for (APIMtd mtd : clazz.getMethod())
				{
					String methodBaseName = getMethodbaseName(mtd);
					String methodDesc = getMethodDescription(mtd);
					List<String> resultList = performSearch(clazz.getName(), methodBaseName, methodDesc);
					buff.append(resultFormatter(methodBaseName, methodDesc,resultList));
				}
				FileUtil.writeStringtoFile(EVAL_FOLDER + clazz.getName().trim()+".txt", buff.toString());
				buff.delete(0, buff.length());
			}
		}
		
		//FileUtil.writeStringtoFile(EVAL_FILE, buff.toString());
	}

	
	private List<String> performSearch(String clazz, String methodBaseName, String methodDesc) {
		
		List<String> result = new ArrayList<String>();
		try{
			Query query = createQuery1(clazz, methodBaseName, methodDesc);
			System.out.println(query);
			result = searcher(query);
			if(result.size()==0)
			{
				query = createQuery2(clazz, methodBaseName, methodDesc);
				result = searcher(query);
				if(result.size()==0)
				{
					query = createQuery3(clazz, methodBaseName, methodDesc);
					result = searcher(query);
					if(result.size()==0)
					{
						query = createQuery4(clazz, methodBaseName, methodDesc);
						result = searcher(query);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		return result;
	}


	/**
	 * @param mtd
	 * @return
	 */
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


	/**
	 * @param mtd
	 * @return
	 */
	private String getMethodDescription(APIMtd mtd) {
		String desclist[] = mtd.getDescription().split("\\.");
		String methodDesc = "a";
		methodDesc = desclist[0]==null?"a":desclist[0];
		return methodDesc;
	}

	/**
	 * @param buff
	 * @param mtdBaseName
	 * @param mtdDesc
	 * @param resLst
	 */
	private String resultFormatter(String mtdBaseName, String mtdDesc, List<String> resLst) {
		StringBuffer buff = new StringBuffer();
		buff.append(mtdBaseName);
		buff.append("\n");
		buff.append(mtdDesc);
		buff.append("\n");
		for(int cnt =0; cnt<resLst.size();cnt++)
		{
			buff.append("\t");
			buff.append(cnt);
			buff.append(":\t");
			buff.append(resLst.get(cnt));
			buff.append("\n");
		}
		return buff.toString();
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	private List<String> searcher(Query query) throws Exception 
	{
		// System.err.println("here");
		List<String> retList = new ArrayList<>();
		int hitsPerPage = 10;
		Directory index = FSDirectory.open(new File(IDX_LOCATION));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) 
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			retList.add(getResultString(d));
		}
		return retList;
	}


	/**
	 * @param document
	 * @return Formated String of result based upon type of evaluation
	 */
	private String getResultString(Document document) 
	{
		switch (evalType) 
		{
			case 1:
				return document.get("MTDDESCRIPTION");
			case 2:
				return document.get("CLASSNAME")+"_"+document.get("METHODBASENAME");
			default:
				break;
		}
		return document.get("MTDDESCRIPTION");
	}
}
