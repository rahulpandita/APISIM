package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

/**
 * Abstract Class for creating the template for an Indexing
 * @author Rahul Pandita
 *
 */
public abstract class Indexer<T>
{
	private IndexWriter indexWriter = null;

	protected Analyzer analyser = new EnglishAnalyzer(ver);
	
	private static final Version ver = Configuration.LUCENE_VERSION;
	
	protected String idx_path = "";
	
	public IndexWriter getIndexWriter() throws IOException {
		return getIndexWriter(idx_path);
	}
	
	public IndexWriter getIndexWriter(String idx_path) throws IOException {
		return getIndexWriter(idx_path, OpenMode.CREATE);
	}
	
	public IndexWriter getIndexWriter(String idx_path, OpenMode mode) throws IOException {
		return getIndexWriter(idx_path, mode, ver);
	}
	
	public IndexWriter getIndexWriter(String idx_path, OpenMode mode, Version ver) throws IOException {
		return getIndexWriter(idx_path, mode, ver, analyser);
	}
	
	public IndexWriter getIndexWriter(String idx_path, OpenMode mode, Version ver, Analyzer analyser) throws IOException {
		if (indexWriter == null) 
		{
			IndexWriterConfig config = new IndexWriterConfig(ver, analyser);
			Directory index = FSDirectory.open(new File(idx_path));
			config.setOpenMode(mode);
			indexWriter = new IndexWriter(index, config);
		}
		return indexWriter;
	}
	
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}
	
	public void rebuildIndexes() throws Exception {
		//
		// Erase existing index
		//
		getIndexWriter();
		
		List<T> objList = readObjects();
		for (T idxObj : objList) {
			indexObject(idxObj);
		}

		closeIndexWriter();
	}
	
	public abstract List<T> readObjects() throws Exception;
	
	public abstract Document createDocument(T t);
	
	public Analyzer getAnalyser() {
		return analyser;
	}

	public void setAnalyser(Analyzer analyser) {
		this.analyser = analyser;
	}

	public String getIdx_path() {
		return idx_path;
	}

	public void setIdx_path(String idx_path) {
		this.idx_path = idx_path;
	}
	
	public void indexObject(T t) throws IOException
	{
		IndexWriter writer = getIndexWriter();
		Document doc = createDocument(t);
		writer.addDocument(doc);
	}
	
	protected String clean(String returnType) {
		return returnType==null?"":returnType;
	}
}
