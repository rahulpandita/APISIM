package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;


public class APIMtdIndexer extends Indexer<APIMtd>{
	
	private APITYPE type = APITYPE.UNKNOWN;
	
	public APIMtdIndexer() throws IOException 
	{
		idx_path = Configuration.API_IDX_FILE;
	}
	
	public APIMtdIndexer(APITYPE type) {
		idx_path = Configuration.PROJECT_PATH + "idx" + File.separator + type.name().trim().toLowerCase();
		this.type = type;
	}

	@Override
	public List<APIMtd> readObjects() throws Exception {
		List<APIMtd> mtdList = new ArrayList<>();
		
		List<APIType> clazzList = new ArrayList<>();
		
		if(type.equals(APITYPE.ANDROID))
			clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		else if(type.equals(APITYPE.MIDP)||type.equals(APITYPE.CLDC))
		{	
			clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		}
		else if(type.equals(APITYPE.JAVA))	
			clazzList.addAll(AllClassCrawler.read(Configuration.JAVA_DUMP_PATH));
		else if(type.equals(APITYPE.DOTNET))
			clazzList.addAll(AllClassCrawler.read(Configuration.DOTNET_DUMP_PATH));
		else if(type.equals(APITYPE.ECLIPSE))
			clazzList.addAll(AllClassCrawler.read(Configuration.ECLIPSE_DUMP_PATH));
		else
		{
			clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.JAVA_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.DOTNET_DUMP_PATH));
			clazzList.addAll(AllClassCrawler.read(Configuration.ECLIPSE_DUMP_PATH));
		}
		for (APIType clazz : clazzList) {
			mtdList.addAll(clazz.getConstructors());
			mtdList.addAll(clazz.getMethod());
		}
		return mtdList;
	}

	
	@Override
	public Document createDocument(APIMtd mtd) {
		
		Document doc = new Document();
		
		//API NAME
		doc.add(new TextField(Configuration.IDX_FIELD_API_NAME, mtd.getParentClass().getApiName(), Field.Store.YES));
		
		//CLASS RELATED
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_NAME, getParentClassName(mtd), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_BASE_NAME, clean(mtd.getParentClass().getName()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_NAME_PKG_SPLIT, getParentClassName(mtd).replaceAll("\\.", " "), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT, mtd.getParentClass().getPackage().replaceAll("\\.", " ") + " " + StringUtil.splitCamelCase(mtd.getParentClass().getName()), Field.Store.YES));
		doc.add(new Field(Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT+"_VEC", preprocess(mtd.getParentClass().getPackage().replaceAll("\\.", " ") + " " + StringUtil.splitCamelCase(mtd.getParentClass().getName())), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_PKG_NAME, mtd.getParentClass().getPackage(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_DESCRIPTION, mtd.getParentClass().getSummary()==null?"":mtd.getParentClass().getSummary(), Field.Store.YES));
		//METHOD RELATED
		doc.add(new TextField(Configuration.IDX_FIELD_MODIFIER,  clean(mtd.getModifier()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_NAME, mtd.getName(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_DESCRIPTION, mtd.getDescription(), Field.Store.YES));
		doc.add(new Field(Configuration.IDX_FIELD_DESCRIPTION+"_VEC", preprocess(mtd.getDescription()), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_RETURN, clean(mtd.getReturnType()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_PARAMS,getTypeListasString(mtd.getParameterList()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_EXCEPTIONS,getTypeListasString(mtd.getExceptionList()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL, getParentClassName(mtd) + " " + mtd.getName() + " "	+ mtd.getDescription(), Field.Store.YES));
		
		//OTHER
		doc.add(new TextField(Configuration.IDX_FIELD_ANNOTATED,  clean(String.valueOf(mtd.isTypeAnnotated())), Field.Store.YES));
		decorator(doc, mtd);
		
		return doc;
	}
	
	private void decorator(Document doc, APIMtd mtd) {
		String name = ASTBuilder.getJavaMethodName(mtd.getName());
		if(name==null)
			name = mtd.getName();
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME,name, Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT, StringUtil.splitCamelCase(name), Field.Store.YES));
		doc.add(new Field(Configuration.IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT+"_VEC", preprocess(StringUtil.splitCamelCase(name)), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
		
	}
	
	static String preprocess(String word)
	{
		if(word.trim().equalsIgnoreCase(""))
			return word;
		EnglishAnalyzer en_an = new EnglishAnalyzer(Configuration.LUCENE_VERSION);
		QueryParser parser = new QueryParser(Configuration.LUCENE_VERSION, "$", en_an);
		try {
			word = QueryParser.escape(word.toLowerCase());
			String[] wrdArr = word.split("\\s+");
			int wrdCount = wrdArr.length;
			//System.err.println(wrdCount);
			StringBuffer buff = new StringBuffer();
			int to;
			for(int i=0; i<wrdCount;i=i+500)
			{
				to = i+500;
				if(i+500>wrdCount)
					to = wrdCount;
				
				String[] arr = Arrays.copyOfRange(wrdArr, i, to);
				buff = buff.append(parser.parse(String.join(" ", arr)).toString("$")).append(" ");
				
			}
			return(buff.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return word;
	}
	
	/**
	 * @param apiTypeList
	 * @return
	 */
	private String getTypeListasString(List<String> apiTypeList) {
		StringBuffer buff = new StringBuffer();
		if(apiTypeList!=null)
		{
			for(String tmp: apiTypeList)
			{
				buff.append(tmp);
				buff.append(" ");
			}
		}
		return buff.toString().trim();
	}

	/**
	 * @param APIMtd
	 * @return
	 */
	private String getParentClassName(APIMtd APIMtd) {
		return APIMtd.getParentClass().getPackage() +"."+ APIMtd.getParentClass().getName();
	}
}
