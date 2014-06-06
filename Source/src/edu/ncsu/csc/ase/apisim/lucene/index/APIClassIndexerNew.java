package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

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
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * Indexer class to index API class description.
 * @author Rahul Pandita
 *
 */
public class APIClassIndexerNew {

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
		try{
			doc.add(new TextField("parentClass", apiClass.getParentClass().getPackage()+"."+apiClass.getParentClass().getName(),	Field.Store.YES));
		}
		catch(NullPointerException e)
		{
			doc.add(new TextField("parentClass", "",	Field.Store.YES));
		}
		String type ="class";
		if(apiClass.isEnums())
			type = "enum";
		if(apiClass.isInterfaze())
			type = "interface";
		
		doc.add(new TextField("type", type,	Field.Store.YES));
		
		StringBuffer buff = new StringBuffer();
		for(String tmp: apiClass.getImplementsList())
		{
			buff.append(tmp);
			buff.append(" ");
		}
		
		doc.add(new TextField("IMPL", buff.toString().trim(),	Field.Store.YES));
		
		buff = new StringBuffer();
		for(String tmp: apiClass.getExtendsList())
		{
			buff.append(tmp);
			buff.append(" ");
		}
		
		doc.add(new TextField("EXTENDS", buff.toString().trim(),	Field.Store.YES));
		
		writer.addDocument(doc);
	}

	public void rebuildIndexes() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter(true);
		
		ArrayList<APIType> clazzList = read(Configuration.ANDROID_DUMP_PATH+"111");
		for (APIType clazz : clazzList) {
			indexAPIClass(clazz);
		}

		closeIndexWriter();
	}
	
	public static ArrayList<APIType> read(String file) {
		ArrayList<APIType> classList = new ArrayList<>();
		APIType clazz;
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			while (fin.available() > 0) {
				clazz = (APIType) ois.readObject();
				classList.add(clazz);
			}
			ois.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.err.println("Read " + classList.size() + " documents from file");
		return classList;
	}

	public static void main(String[] args) {
		APIClassIndexerNew idxr = new APIClassIndexerNew();
		
		try {
			idxr.analyser = new SynonymAnalyzer();
			idxr.idx_path = Configuration.API_IDX_CLAZZ_NEW;
			idxr.rebuildIndexes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
