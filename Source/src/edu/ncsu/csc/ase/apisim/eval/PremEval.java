package edu.ncsu.csc.ase.apisim.eval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

/**
 * 
 * @author rahulpandita
 *
 */
public class PremEval 
{
	
	
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
		BooleanClause.Occur bc1 = BooleanClause.Occur.MUST;
		BooleanClause.Occur bc2 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur bc3 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur bc4 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur[] flags = { bc1, bc2, bc4 };
		int i =0;
		for (APIType clazz : clazzList) 
		{
			if(clazz.getName().equalsIgnoreCase("graphics"))
			{
				for (APIMtd mtd : clazz.getMethod())
				{
					
					String methodNameTrunc = mtd.getName().substring(0, mtd.getName().indexOf('(')).trim();
					String[] methodTokens = methodNameTrunc.split("\\s+");
					String methodBaseName = mtd.getName();
					if(methodTokens.length >= 2) 
						methodBaseName = methodTokens[methodTokens.length-1];
					
					String desclist[] = mtd.getDescription().split("\\.");
					String methodDesc = "a";
					if(desclist[0]!=null)
						desclist = desclist[0].split("\\n");
					methodDesc = desclist[0]==null?"a":desclist[0];
					/*
					 	CLASSNAME1:canvas*
						+APINAME: android
						METHODNAME:getHeight
						MTDDESCRIPTION:Gets height of the displayable area in pixels.
					 */
					methodBaseName = MultiFieldQueryParser.escape(methodBaseName);
					methodDesc = MultiFieldQueryParser.escape(methodDesc);
					String[] termVector = new String[] {"android", "graphics*", methodBaseName, methodDesc};
					String[] columnVector = new String[] {"APINAME","CLASSNAME1","METHODNAME","MTDDESCRIPTION1"};
					termVector = new String[] {"android", "graphics*", methodDesc};
					columnVector = new String[] {"APINAME","CLASSNAME1","MTDDESCRIPTION1"};
					
					try{
						i=i+1;
						Query query = MultiFieldQueryParser.parse(Version.LUCENE_47, termVector, columnVector, flags, new EnglishAnalyzer(Version.LUCENE_47));
						System.out.println(query);
						System.out.println(i);
						List<String> result = searcher(query);
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
						
					}
					catch(Exception e)
					{
					e.printStackTrace();	
					}
					
					
				}
			}
			
		}
		
		BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File("tst.txt");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(buff.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static List<String> searcher(Query query) throws Exception
	{
		//System.err.println("here");
				List<String> retList = new ArrayList<>();
				int hitsPerPage = 10;
				Directory index = FSDirectory.open(new File(Configuration.API_IDX_FILE_SYNONYM));
				IndexReader reader = DirectoryReader.open(index);
				IndexSearcher searcher = new IndexSearcher(reader);
				TopScoreDocCollector collector = TopScoreDocCollector.create(
						hitsPerPage, true);
				searcher.search(query, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				for (int i = 0; i < 10; ++i) 
				{
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					retList.add(d.get("MTDDESCRIPTION"));
				}
				return retList;
	}
	
}
