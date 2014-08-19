package edu.ncsu.csc.ase.apisim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.eval.util.CustomComparator;
import edu.ncsu.csc.ase.apisim.eval.util.ResultRep;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

/**
 * This class is responsible for the Preliminary Evaluation of API similarity project
 * @author Rahul Pandita
 *
 */
public class Eval3 extends PremEval<APIMtd> 
{
	
	private List<String> subClassList;
	
	private List<APIType> clazzList ;
	
	public Eval3(List<APIType> clazzList, String idxFileLoc) 
	{
		super(idxFileLoc);
		initialize(clazzList);
	}
	
	public Eval3(List<APIType> clazzList, String idxFileLoc, Analyzer analyzer) 
	{
		super(idxFileLoc, analyzer);
		initialize(clazzList);
	}


	private void initialize(List<APIType> clazzList) {
		this.clazzList = clazzList;
		this.subClassList = new ArrayList<String>();
		this.hitsPerPage = 100;
		subClassList.add("Alert");
		subClassList.add("Command");
		subClassList.add("Font");
		subClassList.add("Displayable");
		subClassList.add("Display");
		subClassList.add("Canvas");
		//subClassList.add("GameCanvas");
		//subClassList.add("Layer");
		//subClassList.add("Sprite");
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
		Map<String , ResultRep> docMap = new LinkedHashMap<String, ResultRep>();
		
		try
		{
			Float avg = new Float(queryList.size());
			Float rank;
			for(Query query: queryList)
			{
				result = searcher(query);
				for(int i= 0; i<result.size();i++)
				{
					Document doc = result.get(i);
					String key = doc.get(Configuration.IDX_FIELD_METHOD_DESCRIPTION_CATCHALL);
					rank = Float.parseFloat(doc.get("SCORE"));
					rank = rank/avg;
					if(!docMap.containsKey(key))
						docMap.put(key, new ResultRep(doc, rank));
					else
					{
						ResultRep rep = docMap.get(key);
						rep.setRank(rep.getRank() + rank);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		
		TreeSet<ResultRep> set = new TreeSet<ResultRep>(new CustomComparator());
		
		for(String key: docMap.keySet())
			set.add(docMap.get(key));
		
		result = new ArrayList<Document>();
		Iterator<ResultRep> iter = set.descendingIterator();
		ResultRep rep;
		Set<String> tgtset = getTgtSet(); 
		while(iter.hasNext())
		{
			rep = iter.next();
			if(tgtset.contains(rep.getDoc().get(Configuration.IDX_FIELD_PKG_NAME).trim()))
				result.add(rep.getDoc());
		}
		
		
		if(result.size()>10)
			result = result.subList(0, 10);
		return result;
	}
	private static Set<String> tgtset;
	
	private synchronized Set<String> getTgtSet() {
		if(tgtset==null)
		{
			tgtset = new HashSet<String>();
			tgtset.add("android.accessibilityservice");
			tgtset.add("android.animation");
			tgtset.add("android.app");
			tgtset.add("android.app.backup");
			tgtset.add("android.bluetooth");
			tgtset.add("android.content");
			tgtset.add("android.content.pm");
			tgtset.add("android.content.res");
			tgtset.add("android.database");
			tgtset.add("android.drm");
			tgtset.add("android.gesture");
			tgtset.add("android.graphics");
			tgtset.add("android.graphics.drawable");
			tgtset.add("android.graphics.drawable.shapes");
			tgtset.add("android.graphics.pdf");
			tgtset.add("android.hardware");
			tgtset.add("android.hardware.display");
			tgtset.add("android.hardware.input");
			tgtset.add("android.hardware.location");
			tgtset.add("android.inputmethodservice");
			tgtset.add("android.location");
			tgtset.add("android.media.effect");
			tgtset.add("android.mtp");
			tgtset.add("android.net.http");
			tgtset.add("android.nfc");
			tgtset.add("android.nfc.cardemulation");
			tgtset.add("android.nfc.tech");
			tgtset.add("android.opengl");
			tgtset.add("android.preference");
			tgtset.add("android.print");
			tgtset.add("android.print.pdf");
			tgtset.add("android.printservice");
			tgtset.add("android.renderscript");
			tgtset.add("android.sax");
			tgtset.add("android.security");
			tgtset.add("android.service.dreams");
			tgtset.add("android.speech");
			tgtset.add("android.support.v13.app");
			tgtset.add("android.support.v4.app");
			tgtset.add("android.support.v4.net");
			tgtset.add("android.support.v4.os");
			tgtset.add("android.support.v4.view");
			tgtset.add("android.support.v4.view.accessibility");
			tgtset.add("android.support.v4.widget");
			tgtset.add("android.support.v7.app");
			tgtset.add("android.support.v7.media");
			tgtset.add("android.support.v7.view");
			tgtset.add("android.support.v7.widget");
			tgtset.add("android.support.v8.renderscript");
			tgtset.add("android.telephony");
			tgtset.add("android.telephony.cdma");
			tgtset.add("android.telephony.gsm");
			tgtset.add("android.test");
			tgtset.add("android.text");
			tgtset.add("android.text.method");
			tgtset.add("android.text.style");
			tgtset.add("android.text.util");
			tgtset.add("android.transition");
			tgtset.add("android.util");
			tgtset.add("android.view");
			tgtset.add("android.view.accessibility");
			tgtset.add("android.view.animation");
			tgtset.add("android.view.inputmethod");
			tgtset.add("android.widget");
			tgtset.add("java.awt.font");
			tgtset.add("java.beans");
			tgtset.add("java.io");
			tgtset.add("java.lang");
			tgtset.add("java.lang.reflect");
			tgtset.add("java.math");
			tgtset.add("java.nio");
			tgtset.add("java.nio.channels");
			tgtset.add("java.nio.channels.spi");
			tgtset.add("java.security");
			tgtset.add("java.security.acl");
			tgtset.add("java.security.interfaces");
			tgtset.add("java.text");
			tgtset.add("java.util.concurrent.atomic");
			tgtset.add("java.util.concurrent.locks");
			tgtset.add("java.util.jar");
			tgtset.add("java.util.regex");
			tgtset.add("javax.crypto");
			tgtset.add("javax.security.auth");
			tgtset.add("javax.security.cert");
			tgtset.add("javax.sql");
			tgtset.add("javax.xml.datatype");
			tgtset.add("javax.xml.parsers");
			tgtset.add("javax.xml.transform.dom");
			tgtset.add("org.apache.http.auth");
			tgtset.add("org.apache.http.client.methods");
			tgtset.add("org.apache.http.client.protocol");
			tgtset.add("org.apache.http.conn");
			tgtset.add("org.apache.http.conn.params");
			tgtset.add("org.apache.http.impl.conn");
			tgtset.add("org.apache.http.impl.conn.tsccm");
			tgtset.add("org.apache.http.impl.cookie");
			tgtset.add("org.apache.http.impl.entity");
			tgtset.add("org.apache.http.impl.io");
			tgtset.add("org.apache.http.io");
			tgtset.add("org.apache.http.message");
			tgtset.add("org.apache.http.util");
			tgtset.add("org.json");
			tgtset.add("org.w3c.dom");
			tgtset.add("org.xml.sax");
			tgtset.add("org.xml.sax.ext");
			tgtset.add("org.xml.sax.helpers");
		}
		return tgtset;
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
				Configuration.IDX_FIELD_CLASS_BASE_NAME,
				Configuration.IDX_FIELD_METHOD_NAME,
				Configuration.IDX_FIELD_DESCRIPTION};
		
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
