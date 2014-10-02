package edu.ncsu.csc.ase.apisim.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.OOTYPE;
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
		
		doc.add(new StringField(Configuration.IDX_FIELD_CLASS_NAME, clean(apiType.getPackage() + "." + apiType.getName()), Field.Store.YES));
		doc.add(new StringField(Configuration.IDX_FIELD_CLASS_BASE_NAME_CCSPLIT, clean(StringUtil.splitCamelCase(apiType.getName())), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_DESCRIPTION, clean(apiType.getSummary()),	Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_API_NAME, clean(apiType.getApiName()),	Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_OO_TYPE, clean(getElementType(apiType).name()),	Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_IMPLEMENTS, clean(getTypeListasString(apiType.getImplementsList())), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_EXTENDS,  clean(getTypeListasString(apiType.getExtendsList())), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_ANNOTATED,  clean(String.valueOf(apiType.isTypeAnnotated())), Field.Store.YES));
		doc.add(new TextField(Configuration.IDX_FIELD_MODIFIER,  clean(apiType.getModifier()), Field.Store.YES));
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
	private OOTYPE getElementType(APIType apiType) {
		OOTYPE type = OOTYPE.CLASS;
		if(apiType.isEnums())
			type = OOTYPE.ENUM;
		if(apiType.isInterfaze())
			type = OOTYPE.INTERFACE;
		return type;
	}
	
	@Override
	public List<APIType> readObjects() throws Exception {
		List<APIType> clazzList = new ArrayList<>();
		
		clazzList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		clazzList.addAll(AllClassCrawler.read(Configuration.JAVA_DUMP_PATH));
		return clazzList;
	}
}
