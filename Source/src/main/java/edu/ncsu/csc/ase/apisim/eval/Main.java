package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class Main 
{
	private static final String ANA_ENG = "ENGANA";
	private static final String ANA_SYN = "SYNANA";
	private static final String IDX_ENG = "SIMPLEIDX";
	private static final String IDX_SYN = "SYNIDX";
	private static final String RESULT_BASE = "premResults";
	
	public static void main(String[] args) throws Exception {
		eval1(RESULT_BASE + File.separator + "v1");
		eval2(RESULT_BASE + File.separator + "v2");
		eval2_1(RESULT_BASE + File.separator + "v3");
		eval2_2(RESULT_BASE + File.separator+ "v4");
		
	}
	
	public static void eval1(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_ENG + ".xls";
		PremEval<APIMtd> eval = new Eval1(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_ENG + ".xls";
		eval = new Eval1(clazzList,Configuration.API_IDX_FILE_SYNONYM);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		Analyzer analyser = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_SYN + ".xls";
		eval = new Eval1(clazzList, Configuration.API_IDX_FILE, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_SYN + ".xls";
		eval = new Eval1(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_ENG + ".xls";
		PremEval<APIMtd> eval = new Eval2(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_ENG + ".xls";
		eval = new Eval2(clazzList,Configuration.API_IDX_FILE_SYNONYM);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		Analyzer analyser = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_SYN + ".xls";
		eval = new Eval2(clazzList, Configuration.API_IDX_FILE, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_SYN + ".xls";
		eval = new Eval2(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2_1(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_ENG + ".xls";
		PremEval<APIMtd> eval = new Eval2_1(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_ENG + ".xls";
		eval = new Eval2_1(clazzList,Configuration.API_IDX_FILE_SYNONYM);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		Analyzer analyser = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_SYN + ".xls";
		eval = new Eval2_1(clazzList, Configuration.API_IDX_FILE, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_SYN + ".xls";
		eval = new Eval2_1(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2_2(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_ENG + ".xls";
		PremEval<APIMtd> eval = new Eval2_2(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_ENG + ".xls";
		eval = new Eval2_2(clazzList,Configuration.API_IDX_FILE_SYNONYM);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		Analyzer analyser = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		
		evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_SYN + ".xls";
		eval = new Eval2_2(clazzList, Configuration.API_IDX_FILE, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ IDX_SYN + "_" + ANA_SYN + ".xls";
		eval = new Eval2_2(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	
}
