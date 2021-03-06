package edu.ncsu.csc.ase.apisim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class Eval1 extends PremEval<APIMtd> 
{
	
	private List<String> subClassList;
	
	private List<APIType> clazzList ;
	
	public Eval1(List<APIType> clazzList, String idxFileLoc) 
	{
		super(idxFileLoc);
		initialize(clazzList);
	}
	
	public Eval1(List<APIType> clazzList, String idxFileLoc, Analyzer analyzer) 
	{
		super(idxFileLoc, analyzer);
		initialize(clazzList);
	}


	private void initialize(List<APIType> clazzList) {
		this.clazzList = clazzList;
		this.subClassList = new ArrayList<String>();
		subClassList.add("Alert");
		subClassList.add("Command");
		subClassList.add("Font");
		subClassList.add("Displayable");
		subClassList.add("Display");
		subClassList.add("Canvas");
		subClassList.add("GameCanvas");
		subClassList.add("Layer");
		subClassList.add("Sprite");
		subClassList.add("Graphics");
		subClassList.add("Image");
	}
	
	public boolean inclusionCriteriaMethod(APIMtd mtd) {
		return true;
	}

	public boolean inclusionCriteriaClass(APIType clazz) {
		return subClassList.contains(clazz.getName().trim());
	}

	private String getMethodbaseName(APIMtd mtd) 
	{
		String name = ASTBuilder.getJavaMethodName(mtd.getName());
		if(name==null||name.length()==0)
		{
			String methodNameTrunc = mtd.getName().substring(0, mtd.getName().indexOf('(')).trim();
			String[] methodTokens = methodNameTrunc.split("\\s+");
			String methodBaseName = mtd.getName();
			if(methodTokens.length >= 2) 
				methodBaseName = methodTokens[methodTokens.length-1];
			else
				methodBaseName = methodNameTrunc;
			return methodBaseName;
		}
		return name;
	}
	
	private String getMethodDescription(APIMtd mtd) {
		String desclist[] = mtd.getDescription().split("\\.");
		String methodDesc = "a";
		methodDesc = desclist[0]==null?"a":desclist[0];
		return methodDesc;
	}

	@Override
	public List<Document> customRanking(List<Query> queryList) throws Exception {
		List<Document> result = new ArrayList<Document>();
		try
		{
			for(Query query: queryList)
			{
				System.out.println(query);
				result = searcher(query);
				if(result.size()!=0)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		return result;
	}

	@Override
	public List<Occur[]> getClauseVector() 
	{
		List<Occur[]> clauseList = new ArrayList<Occur[]>();
		Occur[] occur1 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur2 = {Occur.MUST, Occur.MUST, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur3 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};
		Occur[] occur4 = {Occur.MUST, Occur.SHOULD, Occur.SHOULD, Occur.SHOULD};

		clauseList.add(occur1);
		clauseList.add(occur2);
		clauseList.add(occur3);
		clauseList.add(occur4);
		
		return clauseList;
	}

	@Override
	public List<String[]> getColumnVector() {
		List<String[]> columnList = new ArrayList<String[]>();
		String[] columnVec = new String[] {
				Configuration.IDX_FIELD_API_NAME,
				Configuration.IDX_FIELD_CLASS_NAME_PKG_SPLIT,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL};
		
		columnList.add(columnVec);
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		columnList.add(Arrays.copyOf(columnVec, 4));
		
		return columnList;
	}

	@Override
	public List<String[]> getTermVector(APIMtd mtd) {
		List<String[]> termList = new ArrayList<String[]>();
		String apiName = MultiFieldQueryParser.escape(APITYPE.ANDROID.name().toLowerCase());
		String clazzName = MultiFieldQueryParser.escape(mtd.getParentClass().getName());
		String methodBaseName = MultiFieldQueryParser.escape(getMethodbaseName(mtd));
		String methodDesc = MultiFieldQueryParser.escape(getMethodDescription(mtd));
		methodDesc = methodDesc.replaceAll("\n", "");
		methodDesc = methodDesc.trim()==""?" ":methodDesc;
		
		String[] termVec1 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec2 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		String[] termVec3 = new String[] {apiName, clazzName, methodBaseName, methodDesc};
		String[] termVec4 = new String[] {apiName, clazzName+"*", methodBaseName, methodDesc};
		
		termList.add(termVec1);
		termList.add(termVec2);
		termList.add(termVec3);
		termList.add(termVec4);
		
		return termList;
	}

	@Override
	public Map<String, List<APIMtd>> inclusionList() {
		Map<String, List<APIMtd>> returnMap = new HashMap<String, List<APIMtd>>();
		for(APIType clazz: clazzList)
		{
			if(inclusionCriteriaClass(clazz))
			{
				List<APIMtd> mtdList = new ArrayList<APIMtd>();
				for(APIMtd mtd:clazz.getMethod())
				{
					if(inclusionCriteriaMethod(mtd))
					{
						mtdList.add(mtd);
					}
				}
				returnMap.put(clazz.getName(), mtdList);
			}
		}
		return returnMap;
	}
	
}
