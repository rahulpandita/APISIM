package edu.ncsu.csc.ase.apisim.lucene.searcher;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.spring.mvc.displayBeans.Result;
import edu.ncsu.csc.ase.apisim.util.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.util.ConsoleUtil;
import edu.ncsu.csc.ase.apisim.util.FileUtilExcel;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIMethod;

public class APISearcher {
	public static String queryTerm = "";

	public static void main(String[] args) {

		APISearcher searcher = new APISearcher();
		String searchTerm;
		BooleanClause.Occur bc1 = BooleanClause.Occur.MUST;
		BooleanClause.Occur bc2 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur[] flags = { bc1, bc2 };

		try {
			for (APIClass clazz : AllClassCrawler
					.read(Configuration.MIDP_DUMP_PATH)) {
				 if((clazz.getPackage().trim() + "." +
				 clazz.getName().trim()).equalsIgnoreCase("javax.microedition.lcdui.Graphics"))
				{
					for (APIMethod mtd : clazz.getMethod()) {
						{
						FileUtilExcel.getInstance().writeDataToExcel(
								clazz.getPackage() + "." + clazz.getName(),
								mtd.getName(), mtd.getDescription());
						searchTerm = clazz.getPackage() + "." + clazz.getName()
								+ " " + mtd.getName() + " "
								+ mtd.getDescription();
						searchTerm = MultiFieldQueryParser.escape(searchTerm);
						queryTerm = searchTerm;
						String[] query = { "android", queryTerm };
						String[] fields = { Configuration.IDX_FIELD_API_NAME,
								Configuration.IDX_FIELD_METHOD_DESCRIPTION };
						System.out.println(searchTerm);
						ConsoleUtil.readConsole("");
						try {
							Query q = MultiFieldQueryParser.parse(
									Version.LUCENE_44, query, fields, flags,
									new EnglishAnalyzer(Version.LUCENE_44));
							searcher.search(q);
						} catch (Exception e) {
							// Double the number of boolean queries allowed.
							// The default is in
							// org.apache.lucene.search.BooleanQuery and is
							// 1024.
							//System.err.println(searchTerm);
							String defaultQueries = Integer
									.toString(BooleanQuery.getMaxClauseCount());
							int oldQueries = Integer.parseInt(System
									.getProperty(
											"org.apache.lucene.maxClauseCount",
											defaultQueries));
							int newQueries = oldQueries * 5;

							System.setProperty(
									"org.apache.lucene.maxClauseCount",
									Integer.toString(newQueries));
							BooleanQuery.setMaxClauseCount(newQueries);
							Query q = MultiFieldQueryParser.parse(
									Version.LUCENE_44, query, fields, flags,
									new EnglishAnalyzer(Version.LUCENE_44));
							searcher.search(q);
							System.setProperty(
									"org.apache.lucene.maxClauseCount",
									Integer.toString(oldQueries));
							BooleanQuery.setMaxClauseCount(oldQueries);

						}
						}
					}
				}
			}
			FileUtilExcel.getInstance().commitXlS();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void search1(Query q) throws Exception {
		int hitsPerPage = 10;
		Directory index = FSDirectory
				.open(new File(Configuration.API_IDX_FILE));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			if (hits[i].score > 4) {

				System.err.println(hits[i].score + "-->\n" + queryTerm + "\n\t"
						+ d.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION));
			}
			FileUtilExcel.getInstance().writeDataToExcel("",
					d.get(Configuration.IDX_FIELD_CLASS_NAME),
					d.get(Configuration.IDX_FIELD_METHOD_NAME),
					d.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION));
		}
	}
	
	public List<Result> search(Query q) throws Exception {
		//System.err.println("here");
		List<Result> retList = new ArrayList<>();
		int hitsPerPage = 10;
		Directory index = FSDirectory
				.open(new File(Configuration.API_IDX_FILE));
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			if (hits[i].score > 4) {

				System.err.println(hits[i].score + "-->\n" + queryTerm + "\n\t"
						+ d.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION));
			}
			
			Result res = new Result(
					d.get(Configuration.IDX_FIELD_CLASS_NAME),
					d.get(Configuration.IDX_FIELD_METHOD_NAME),
					d.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION));
			retList.add(res);
			System.out.println(d.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION));
		}
		
		return retList;
	}
	private void searchSimilarAPI(String searchTerm) throws Exception {
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(
				Configuration.API_IDX_FILE)));
		IndexSearcher is = new IndexSearcher(ir);
		MoreLikeThis mlt = new MoreLikeThis(ir);
		// lower some settings to MoreLikeThis will work with very short
		// quotations
		mlt.setAnalyzer(new EnglishAnalyzer(Version.LUCENE_42));
		mlt.setMinTermFreq(2);
		// mlt.setMinDocFreq(1);
		// We need a Reader to create the Query so we'll create one
		// using the string quoteText.
		Reader reader = new StringReader(searchTerm);
		// Create the query that we can then use to search the index
		Query query = mlt.like(reader, "description");
		// Search the index using the query and get the top 5 results
		TopDocs topDocs = is.search(query, 5);
		// Create an array to hold the quotes we are going to
		// pass back to the client
		// List<Quote> foundQuotes = new ArrayList<Quote>();
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.toString());
		}
	}
}
