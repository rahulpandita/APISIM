package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

/**
 * Indexer class to index API class description.
 * @author Rahul Pandita
 *
 */
public class APIClassIndexer extends Indexer<APIType>
{
	public APIClassIndexer() throws IOException 
	{
		idx_path = Configuration.API_IDX_CLAZZ;
	}
	
	/**
	 * @param apiType
	 * @return
	 */
	public Document createDocument(APIType apiType) {
		Document doc = new Document();
		
		doc.add(new StringField("NAME", clean(apiType.getPackage() + "." + apiType.getName()), Field.Store.YES));
		doc.add(new StringField("NAME_SPLIT", clean(StringUtil.splitCamelCase(apiType.getName())), Field.Store.YES));
		doc.add(new TextField("SUMMARY", clean(apiType.getSummary()),	Field.Store.YES));
		doc.add(new TextField("APINAME", clean(apiType.getApiName()),	Field.Store.YES));
		doc.add(new TextField("TYPE", clean(getElementType(apiType)),	Field.Store.YES));
		doc.add(new TextField("IMPLEMENTS", clean(getTypeListasString(apiType.getImplementsList())), Field.Store.YES));
		doc.add(new TextField("EXTENDS",  clean(getTypeListasString(apiType.getExtendsList())), Field.Store.YES));
		doc.add(new TextField("ANNOTATED",  clean(String.valueOf(apiType.isTypeAnnotated())), Field.Store.YES));
		doc.add(new TextField("MODIFIER",  clean(apiType.getModifier()), Field.Store.YES));
		return doc;
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
	 * @param apiType
	 * @return
	 */
	private String getElementType(APIType apiType) {
		String type ="class";
		if(apiType.isEnums())
			type = "enum";
		if(apiType.isInterfaze())
			type = "interface";
		return type;
	}
	
	@Override
	public List<APIType> readObjects() throws Exception {
		List<APIType> clazzList = new ArrayList<>();
		
		clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		return clazzList;
	}
}
