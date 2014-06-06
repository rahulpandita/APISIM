package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;





import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
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
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.util.SimpleSimilarity;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class APIIndexerSyn {

	private IndexWriter indexWriter = null;

	private Analyzer analyser = new EnglishAnalyzer(ver);
	
	private static final Version ver = Version.LUCENE_47;
	
	private String idx_path = "";
	
	public IndexWriter getIndexWriter(boolean create) throws IOException {
		if (indexWriter == null) 
		{
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
	
	public void indexAPIType(APIType APIType) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField("className", APIType.getPackage() + "."
				+ APIType.getName(), Field.Store.YES));
		doc.add(new TextField("description", APIType.getSummary(),
				Field.Store.YES));
		writer.addDocument(doc);
	}

	
	
	public void indexAPIMtd(String apiName, String className,
			APIMtd APIMtd) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField(Configuration.IDX_FIELD_API_NAME, apiName,
				Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_NAME, className,
				Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_NAME, APIMtd
				.getName(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_DESCRIPTION,
				className + " " + APIMtd.getName() + " "
						+ APIMtd.getDescription(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_DESCRIPTION1,
				APIMtd.getDescription(), Field.Store.YES));
		
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_NAME1, className.replaceAll("\\.", " "),
				Field.Store.YES));
		
		String methodNameTrunc = APIMtd.getName().substring(0, APIMtd.getName().indexOf('(')).trim();
		String[] methodTokens = methodNameTrunc.split("\\s+");
		if(methodTokens.length >= 2) {
			String methodReturnType = methodTokens[methodTokens.length - 2];
			String methodBaseName = methodTokens[methodTokens.length - 1];
			String methodParams = APIMtd.getName().substring(APIMtd.getName().indexOf('(') + 1, APIMtd.getName().indexOf(')'));
			
			if (methodReturnType == "") {
				methodReturnType = "void";
			}

			doc.add(new TextField(Configuration.IDX_FIELD_METHOD_RETURN,
				methodReturnType, Field.Store.YES));
		
			doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME,
				methodBaseName, Field.Store.YES));
			
			doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT,
					StringUtil.splitCamelCase(methodBaseName), Field.Store.YES));
			
			doc.add(new TextField(Configuration.IDX_FIELD_METHOD_PARAMS,
					methodParams, Field.Store.YES));
		} else {
			System.err.println(APIMtd.getName());
			doc.add(new TextField(Configuration.IDX_FIELD_METHOD_RETURN,
					"", Field.Store.YES));
			
				doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME,
					APIMtd.getName(), Field.Store.YES));
				
				doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT,
						"", Field.Store.YES));
				
				doc.add(new TextField(Configuration.IDX_FIELD_METHOD_PARAMS,
					"", Field.Store.YES));
		}
		
		writer.addDocument(doc);
	}

	

	public void rebuildIndexes() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter(true);
		
		List<APIType> clazzList = AllClassCrawler
				.read(Configuration.ANDROID_DUMP_PATH);
		for (APIType clazz : clazzList) 
		{
			for (APIMtd mtd : clazz.getMethod()) {
				indexAPIMtd(APITYPE.ANDROID.name().toLowerCase(),
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = AllClassCrawler.read(Configuration.CLDC_DUMP_PATH);
		for (APIType clazz : clazzList) 
		{
			for (APIMtd mtd : clazz.getMethod()) {
				indexAPIMtd(APITYPE.CLDC.name().toLowerCase(),
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		for (APIType clazz : clazzList)
		{
			for (APIMtd mtd : clazz.getMethod())
			{
				indexAPIMtd(APITYPE.MIDP.name().toLowerCase(),
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}
		
		closeIndexWriter();
	}
	
	public void rebuildIndexesRed() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter(true);

		SimpleSimilarity sim = new SimpleSimilarity();
		List<APIType> clazzList = sim.getClazzListAndroidRed();
		for (APIType clazz : clazzList) {
			for (APIMtd mtd : clazz.getMethod()) {
				indexAPIMtd("android",
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = sim.getClazzListMIDPRed();
		for (APIType clazz : clazzList) {
			for (APIMtd mtd : clazz.getMethod()) {
				indexAPIMtd("j2me_midp",
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		closeIndexWriter();
	}
	
	
	
	public static void main(String[] args) {
		APIIndexerSyn idxr = new APIIndexerSyn();
		
		try {
			Map<String, Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
			analyzerPerField.put(Configuration.IDX_FIELD_METHOD_DESCRIPTION, new SynonymAnalyzer());
			idxr.analyser = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(ver), analyzerPerField);
			idxr.idx_path = Configuration.API_IDX_FILE_SELECTIVE_SYNONYM;
			idxr.rebuildIndexes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
