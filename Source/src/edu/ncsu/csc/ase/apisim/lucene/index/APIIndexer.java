package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.util.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.util.SimpleSimilarity;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIMethod;

public class APIIndexer {

	private IndexWriter indexWriter = null;

	public IndexWriter getIndexWriter(boolean create) throws IOException {
		if (indexWriter == null) {
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44,
					new EnglishAnalyzer(Version.LUCENE_44));
			Directory index = FSDirectory.open(new File(
					Configuration.API_IDX_FILE));
			indexWriter = new IndexWriter(index, config);
		}
		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public void indexAPIClass(APIClass apiClass) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField("className", apiClass.getPackage() + "."
				+ apiClass.getName(), Field.Store.YES));
		doc.add(new TextField("description", apiClass.getSummary(),
				Field.Store.YES));
		writer.addDocument(doc);
	}

	public void indexAPIMethod(String apiName, String className,
			APIMethod apiMethod) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		Document doc = new Document();
		doc.add(new StringField(Configuration.IDX_FIELD_API_NAME, apiName,
				Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_NAME, className,
				Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_NAME, apiMethod
				.getName(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_DESCRIPTION,
				className + " " + apiMethod.getName() + " "
						+ apiMethod.getDescription(), Field.Store.YES));
		writer.addDocument(doc);
	}

	public void rebuildIndexes() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter(true);
		
		ArrayList<APIClass> clazzList = AllClassCrawler
				.read(Configuration.ANDROID_DUMP_PATH);
		for (APIClass clazz : clazzList) {
			for (APIMethod mtd : clazz.getMethod()) {
				indexAPIMethod(APITYPE.ANDROID.name().toLowerCase(),
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = AllClassCrawler.read(Configuration.CLDC_DUMP_PATH);
		for (APIClass clazz : clazzList) {
			for (APIMethod mtd : clazz.getMethod()) {
				indexAPIMethod(APITYPE.CLDC.name().toLowerCase(),
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		for (APIClass clazz : clazzList) {
			for (APIMethod mtd : clazz.getMethod()) {
				indexAPIMethod(APITYPE.MIDP.name().toLowerCase(),
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
		ArrayList<APIClass> clazzList = sim.getClazzListAndroidRed();
		for (APIClass clazz : clazzList) {
			for (APIMethod mtd : clazz.getMethod()) {
				indexAPIMethod("android",
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		clazzList = sim.getClazzListMIDPRed();
		for (APIClass clazz : clazzList) {
			for (APIMethod mtd : clazz.getMethod()) {
				indexAPIMethod("j2me_midp",
						clazz.getPackage() + "." + clazz.getName(), mtd);
			}
		}

		closeIndexWriter();
	}

	public static void main(String[] args) {
		APIIndexer idxr = new APIIndexer();
		try {
			idxr.rebuildIndexes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
