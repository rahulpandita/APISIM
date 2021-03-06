package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.eval.util.ResultEmitter;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class Main 
{
	private static final String EXCEL_XTEN = ".xlsx";
	private static final String ANA_ENG = "ENGANA";
	private static final String ANA_SYN = "SYNANA";
	private static final String RESULT_BASE = "premResults";
	private static Analyzer analyser ;
	
	public static void main(String[] args) throws Exception {
		analyser = new SynonymAnalyzer(Configuration.LUCENE_VERSION);
		//eval1(RESULT_BASE + File.separator + "v1");
		//eval2(RESULT_BASE + File.separator + "v2");
		//eval2_1(RESULT_BASE + File.separator + "v3");
		//eval2_2(RESULT_BASE + File.separator+ "v4");
		//eval3(RESULT_BASE + File.separator+ "v5");
		//eval4(RESULT_BASE + File.separator+ "v6");
		//eval5(RESULT_BASE + File.separator+ "v7");
		//eval6(RESULT_BASE + File.separator+ "v8");
		//eval7(RESULT_BASE + File.separator+ "v9");
		//eval8(RESULT_BASE + File.separator+ "v10");
		eval11(RESULT_BASE + File.separator+ "v11");
		//evalRosetta(RESULT_BASE + File.separator+ "v5");
	}
	
	public static void eval1(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval1(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval1(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval2(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval2(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2_1(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval2_1(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval2_1(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval2_2(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval2_2(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval2_2(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval3(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval3(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval3(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval4(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval4(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval4(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval5(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval5(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval5(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval6(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval6(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval6(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval7(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval7(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval7(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval8(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		evalFile = resFolder + File.separator+ ANA_ENG + EXCEL_XTEN;
		PremEval<APIMtd> eval = new Eval8(clazzList, Configuration.API_IDX_FILE);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		evalFile = resFolder + File.separator+ ANA_SYN + EXCEL_XTEN;
		eval = new Eval8(clazzList, Configuration.API_IDX_FILE_SYNONYM, analyser);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
	}
	
	public static void eval11(String resFolder) throws Exception
	{
		String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.JAVA_DUMP_PATH);
		String pkg;
		PremEval<APIMtd> eval;
		
		/*pkg = "java.io";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		pkg = "java.lang";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		pkg = "java.math";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		pkg = "java.net";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		pkg = "java.sql";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);*/
		
		pkg = "java.util";
		evalFile = resFolder + File.separator+ pkg + EXCEL_XTEN;
		eval = new EvalJava(clazzList, Configuration.API_IDX_FILE, pkg);
		ResultEmitter.writeDataToExcel(eval.eval(), evalFile);
		
		
		
		
	}
	public static void evalRosetta(String resFolder) throws Exception
	{
		//String evalFile;
		List<APIType> clazzList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		
		//evalFile = resFolder + File.separator+ IDX_ENG + "_" + ANA_ENG + EXCEL_XTEN;
		RosettaEval eval = new RosettaEval(clazzList, Configuration.API_IDX_FILE);
		eval.evalWrapper();
	}
	
}
