package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;


public class APIMtdIndexer extends Indexer<APIMtd>{

	public APIMtdIndexer() throws IOException 
	{
		idx_path = Configuration.API_IDX_FILE;
	}
	
	public APIMtdIndexer(String idxPath) {
		idx_path = idxPath;
	}

	@Override
	public List<APIMtd> readObjects() throws Exception {
		List<APIMtd> mtdList = new ArrayList<>();
		
		List<APIType> clazzList = new ArrayList<>();
		
		//clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		//clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		//clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		//clazzList.addAll(AllClassCrawler.read(Configuration.JAVA_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.DOTNET_DUMP_PATH));
		//clazzList.addAll(AllClassCrawler.read(Configuration.ECLIPSE_DUMP_PATH));
		
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
		doc.add(new TextField(Configuration.IDX_FIELD_PKG_NAME, mtd.getParentClass().getPackage(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_CLASS_DESCRIPTION, mtd.getParentClass().getSummary()==null?"":mtd.getParentClass().getSummary(), Field.Store.YES));
		//METHOD RELATED
		doc.add(new TextField(Configuration.IDX_FIELD_MODIFIER,  clean(mtd.getModifier()), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_METHOD_NAME, mtd.getName(), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_DESCRIPTION, mtd.getDescription(), Field.Store.YES));
		doc.add(new Field(Configuration.IDX_FIELD_DESCRIPTION+"11", mtd.getDescription(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
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
