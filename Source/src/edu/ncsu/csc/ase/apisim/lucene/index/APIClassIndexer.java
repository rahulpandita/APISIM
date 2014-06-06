package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

/**
 * Indexer class to index API method description.
 * @author rahulpandita
 *
 */
public class APIClassIndexer {

	private IndexWriter indexWriter = null;

	private Analyzer analyser = new EnglishAnalyzer(ver);
	
	private static final Version ver = Version.LUCENE_47;
	
	private String idx_path = "";
	
	public IndexWriter getIndexWriter(boolean create) throws IOException {
		if (indexWriter == null) {
			IndexWriterConfig config = new IndexWriterConfig(ver, analyser);
			Directory index = FSDirectory.open(new File(idx_path));
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			indexWriter = new IndexWriter(index, config);
		}
		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public void indexAPIClass(APIType apiClass) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField("name", apiClass.getPackage() + "." + apiClass.getName(), Field.Store.YES));
		doc.add(new StringField("nameBase", StringUtil.splitCamelCase(apiClass.getName()), Field.Store.YES));
		doc.add(new TextField("desc", apiClass.getSummary(),	Field.Store.YES));
		writer.addDocument(doc);
	}

	public void rebuildIndexes() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter(true);
		
		List<APIType> clazzList = AllClassCrawler
				.read(Configuration.ANDROID_DUMP_PATH);
		for (APIType clazz : clazzList) {
			indexAPIClass(clazz);
		}

		clazzList = AllClassCrawler.read(Configuration.CLDC_DUMP_PATH);
		for (APIType clazz : clazzList) {
			indexAPIClass(clazz);
		}

		clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		for (APIType clazz : clazzList) {
			indexAPIClass(clazz);
		}
		
		closeIndexWriter();
	}

	public static void main(String[] args) {
		APIClassIndexer idxr = new APIClassIndexer();
		
		try {
			idxr.analyser = new SynonymAnalyzer();
			idxr.idx_path = Configuration.API_IDX_CLAZZ;
			idxr.rebuildIndexes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
