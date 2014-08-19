package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.PackageSummaryCrawler;

public class InstanceCreator {

	private static final String STOP_WORD_FILE_LOC = "data\\en.txt"; 
	
	private static List<String> evalList;
	
	public static InstanceList createInstanceList(List<Instance> trainList) throws Exception {
		
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add(new CharSequenceLowercase());
		//pipeList.add(new CharSequenceClean());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File(STOP_WORD_FILE_LOC),"UTF-8", false, false, false));
		pipeList.add(new TokenSequenceClean(false));
		//pipeList.add(new TokenSequenceSyn(false));
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		instances.addThruPipe(trainList.iterator());
		
		return instances;
	}
	
	public synchronized static List<String> getEvalList()
	{
		if(evalList == null)
		{
			evalList = new ArrayList<String>();
			evalList.add("Alert");
			evalList.add("Command");
			evalList.add("Font");
			evalList.add("Displayable");
			evalList.add("Display");
			evalList.add("Canvas");
			evalList.add("GameCanvas");
			evalList.add("Layer");
			evalList.add("Sprite");
			evalList.add("Graphics");
			evalList.add("Image");
		}
		return evalList;
	}
	
	public static InstanceList createInstanceList(boolean includeMtd,String...apiDumpPath) throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		for(String API:apiDumpPath)
		{
			for(APIType type: AllClassCrawler.read(API))
			{
				if(includeMtd)
					insList.add(new DataInstance(true, type));
				else
					insList.add(new DataInstance(type));
			}
		}
		return createInstanceList(insList);
	}
	
	

	public static InstanceList createInstanceList1(boolean includeMtd, List<String> subList, String... apiDumpPath) throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		for(String API:apiDumpPath)
		{
			for(APIType type: AllClassCrawler.read(API))
			{
				if(subList.contains(type.getName().trim()))
				{	
					if(includeMtd)
						insList.add(new DataInstance(true, type));
					else
						insList.add(new DataInstance(type));
				}
			}
		}
		return createInstanceList(insList);
	}
	
	public static InstanceList createInstanceListAndroid() throws Exception
	{
		return InstanceCreator.createInstanceList(getInsListAndroidPkg());
	}

	private static List<Instance> getInsListAndroidPkg() throws Exception {
		return getInsListAndroidPkgCls(new HashMap<String, String>());
	}

	private static HashMap<String, String> readPagesPersistance(String fileName)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		
		File file = new File(fileName);
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    @SuppressWarnings("unchecked")
		HashMap<String, String> pageMap = (HashMap<String, String>) s.readObject();
	    s.close();
		return pageMap;
	}
	
	
	public static InstanceList createInstanceListmidp() throws Exception
	{
		return InstanceCreator.createInstanceList(getInsListMidpPkg());
	}
	
	
	private static List<Instance> getInsListMidpPkg() throws Exception {
		return getInsListMidpPkgCls(new HashMap<String, String>());
	}

	public static InstanceList createInstanceListPkg() throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
	    insList.addAll(getInsListMidpPkg());
	    insList.addAll(getInsListAndroidPkg());
		return InstanceCreator.createInstanceList(insList);
	}
	
	public static InstanceList createInstanceListPkgCls() throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		insList.addAll(getInsListMidpPkgCls(getPkgStrMap(Configuration.MIDP_DUMP_PATH)));
	    insList.addAll(getInsListAndroidPkgCls(getPkgStrMap(Configuration.ANDROID_DUMP_PATH)));
		return InstanceCreator.createInstanceList(insList);
	}

	private static List<Instance> getInsListMidpPkgCls(Map<String, String> pkgMap) throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		
		HashMap<String, String> pageMap = readPagesPersistance(PackageSummaryCrawler.MIDP_PKG_DMP_FILE);
	    String conString;
	    Element  element;
	   
	    StringBuffer buff;
	    boolean flag = false;
	    for(String key:pageMap.keySet())
	    {
	    	conString = pageMap.get(key);
	    	element = Jsoup.parse(conString).body();
	    	
	    	buff = new StringBuffer();
	    	flag = false;
	    	for(Element n:element.children())
	    	{
	    		if((n.hasAttr("name"))&&(n.attr("name").trim().equals("skip-navbar_top"))){
	    			
	    			flag=true;
	    		}
	    		if(flag)
	    		{
	    			if((n.hasAttr("name"))&&(n.attr("name").trim().equals("navbar_bottom"))){
		    			
		    			break;
		    		}
	    			buff.append(n.text());
	    			buff.append(" ");
	    		}
	    	}
	    	
	    	if(pkgMap.get(key)!=null)
	    	{
	    		buff.append("\n");
	    		buff.append(pkgMap.get(key));
	    	}
	    	insList.add(new DataInstance(buff.toString(), key));
	    	//System.out.println(buff.toString());
	    	
	    }
		return insList;
	}

	private static Map<String, String> getPkgStrMap(String apiDumpPath) {
		List<APIType> typeList = AllClassCrawler.read(apiDumpPath);
		Map<String, String> pkgMap = new HashMap<String, String>();
		String pkgStr;
		String data;
		for(APIType type: typeList)
		{
			pkgStr = type.getPackage();
			data = type.getSummary()==null?"":type.getSummary();
			if(!pkgMap.containsKey(pkgStr))
				pkgMap.put(type.getPackage(), "");
			pkgMap.put(pkgStr, pkgMap.get(pkgStr)+"\n"+data);
		}
		return pkgMap;
	}
	
	private static List<Instance> getInsListAndroidPkgCls(Map<String, String> pkgMap) throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		
		HashMap<String, String> pageMap = readPagesPersistance(PackageSummaryCrawler.ANDROID_PKG_DMP_FILE);
	    String conString;
	    Element  element;
	    StringBuffer buff;;
	    for(String key:pageMap.keySet())
	    {
	    	buff= new StringBuffer();
	    	conString = pageMap.get(key);
	    	element = Jsoup.parse(conString).getElementById("jd-content");
	    	buff.append(element.text());
	    	if(pkgMap.get(key)!=null)
	    	{
	    		buff.append("\n");
	    		buff.append(pkgMap.get(key));
	    	}
	    	insList.add(new DataInstance(buff.toString(), key));
	    }
		return insList;
	}

	public static InstanceList createInstanceListmidppkgClass() throws Exception
	{
		return InstanceCreator.createInstanceList(getInsListMidpPkgCls(getPkgStrMap(Configuration.MIDP_DUMP_PATH)));
	}
	
	public static InstanceList createInstanceListandroidpkgClass() throws Exception
	{
		return InstanceCreator.createInstanceList(getInsListAndroidPkgCls(getPkgStrMap(Configuration.ANDROID_DUMP_PATH)));
	}

	public static InstanceList createInstanceList22(boolean includeMtd, String midpDumpPath) throws Exception {
		List<Instance> insList = new ArrayList<Instance>();
		for(APIType type: AllClassCrawler.read(midpDumpPath))
		{
			if (getEvalList().contains(type.getName().trim()))
			{
				if(includeMtd)
					insList.add(new DataInstance(true, type));
				else
					insList.add(new DataInstance(type));
			}
		}
		
		return createInstanceList(insList);
	}
}
