package edu.ncsu.csc.ase.apisim.lucene.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class WordNetCustomParser extends SynonymMap.Parser {

	private WordnetSynonymParser parser;
	public WordNetCustomParser(boolean dedup, boolean expand, Analyzer analyzer) {
		
		super(dedup,new StandardAnalyzer(Version.LUCENE_47,CharArraySet.EMPTY_SET));
		parser = new WordnetSynonymParser(true, true, new StandardAnalyzer(Version.LUCENE_47,CharArraySet.EMPTY_SET));
	}
	
	@Override
	public void parse(Reader arg0) throws IOException, ParseException {
		parser.parse(new InputStreamReader(new FileInputStream(Configuration.DICTIONARY)));
		
	}

	

}
