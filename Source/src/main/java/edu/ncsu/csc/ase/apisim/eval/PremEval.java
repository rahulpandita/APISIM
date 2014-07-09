package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import edu.ncsu.csc.ase.apisim.util.FileUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class PremEval 
{
	
	public static Query createQuery1(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.MUST, false, className, methodBaseName, methodDesc);
	}
	
	public static Query createQuery2(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.MUST, true, className, methodBaseName, methodDesc);
	}
	
	public static Query createQuery3(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.SHOULD, false, className, methodBaseName, methodDesc);
	}
	
	public static Query createQuery4(String className, String methodBaseName, String methodDesc) throws ParseException
	{
		return createQueryBase(Occur.SHOULD, true, className, methodBaseName, methodDesc);
	}
	
	
	private static Query createQueryBase(Occur clause, Boolean wildCard, String className, String methodBaseName, String methodDesc) throws ParseException
	{
		methodBaseName = MultiFieldQueryParser.escape(methodBaseName.toLowerCase());
		methodDesc = MultiFieldQueryParser.escape(methodDesc.toLowerCase());
		className = MultiFieldQueryParser.escape(className.toLowerCase());
		className = wildCard?className+"*":className;
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
		
		String[] termVector = new String[] {"android", className, methodBaseName, methodDesc};
		String[] columnVector = new String[] {"APINAME","CLASSNAME1","METHODNAME","MTDDESCRIPTION"};
		
		Query query = MultiFieldQueryParser.parse(Configuration.LUCENE_VERSION, termVector, columnVector, flags, new EnglishAnalyzer(Configuration.LUCENE_VERSION));
		
		return query;
	}
	
	
	/**
	 * test method to be removed from production code
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception 
	{
		StringBuffer buff = new StringBuffer();
		//Graphics
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		int i =0;
		for (APIType clazz : clazzList) 
		{
			if(clazz.getName().equalsIgnoreCase("alert"))
			{
				for (APIMtd mtd : clazz.getMethod())
				{
					
					String methodNameTrunc = mtd.getName().substring(0, mtd.getName().indexOf('(')).trim();
					String[] methodTokens = methodNameTrunc.split("\\s+");
					String methodBaseName = mtd.getName();
					if(methodTokens.length >= 2) 
						methodBaseName = methodTokens[methodTokens.length-1];
					else
						methodBaseName = methodNameTrunc;
					String desclist[] = mtd.getDescription().split("\\.");
					String methodDesc = "a";
					methodDesc = desclist[0]==null?"a":desclist[0];
					
					try{
						i=i+1;
						Query query = createQuery1(clazz.getName(),methodBaseName, methodDesc);
						System.out.println(query);
						System.out.println(i);
						List<String> result = searcher(query);
						if(result.size()>0)
							buff.append(resultFormatter(methodBaseName, methodDesc,result));
						else
						{
							query = createQuery2(clazz.getName(),methodBaseName, methodDesc);
							result = searcher(query);
							if(result.size()>0)
								buff.append(resultFormatter(methodBaseName, methodDesc,result));
							else
							{
								query = createQuery3(clazz.getName(),methodBaseName, methodDesc);
								result = searcher(query);
								if(result.size()>0)
									buff.append(resultFormatter(methodBaseName, methodDesc,result));
								else
								{
									query = createQuery4(clazz.getName(),methodBaseName, methodDesc);
									result = searcher(query);
									buff.append(resultFormatter(methodBaseName, methodDesc,result));
									
								}
							}
						}
						
					}
					catch(Exception e)
					{
					e.printStackTrace();	
					}
					
					
				}
			}
			
		}
		
		FileUtil.writeStringtoFile("tst.txt", buff.toString());
	}

	/**
	 * @param buff
	 * @param methodBaseName
	 * @param methodDesc
	 * @param result
	 */
	private static String resultFormatter(String methodBaseName, String methodDesc, List<String> result) {
		StringBuffer buff = new StringBuffer();
		buff.append(methodBaseName);
		buff.append("\n");
		buff.append(methodDesc);
		buff.append("\n");
		for(int cnt =0; cnt<result.size();cnt++)
		{
			buff.append("\t");
			buff.append(cnt);
			buff.append(":\t");
			buff.append(result.get(cnt));
			buff.append("\n");
		}
		return buff.toString();
	}
	
	/**
	 * @throws Exception
	 * 
	 */
	public static List<String> searcher(Query query) throws Exception 
	{
		// System.err.println("here");
		List<String> retList = new ArrayList<>();
		int hitsPerPage = 10;
		Directory index = FSDirectory.open(new File(Configuration.API_IDX_FILE_SYNONYM));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) 
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			retList.add(d.get("MTDDESCRIPTION"));
		}
		return retList;
	}
	
}
