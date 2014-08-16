package edu.ncsu.csc.ase.apisim.topicModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.PackageSummaryCrawler;

public class TopicModelPkgClass extends TopicModelFactory {

	public static final String OUTPUT_FILE_NAME_1 = "PkgClassSummaryPerTopic";

	public static final String OUTPUT_FILE_NAME_2 = "PkgClassSummaryPerPkg";

	public static void main(String[] args) {
		TopicModelFactory tm;
		try {
			tm = new TopicModelPkgClass();
			tm.runEval();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TopicModelPkgClass() throws Exception {
		super(500, 2, 2000);

	}

	@Override
	public InstanceList getInstanceList() {

		try {
			return InstanceCreator.createInstanceListandroidpkgClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getTargetInstanceList() {
		try {
			return InstanceCreator.createInstanceListPkgCls();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceList getSearchInstanceList() {
		try {
			return InstanceCreator.createInstanceListmidppkgClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void runEval() throws Exception {
		
		test(getRankedListPerTopic(0.01),
				OUTPUT_FILE_NAME_1, 10);
		test1(getRankedListPerTopic(0.01),
				OUTPUT_FILE_NAME_1, 10);
		//writeToFile(getRankedListPerSearch(0.001), OUTPUT_FILE_NAME_2, 30);
	}
	
	
	public void test(Map<String, List<String>> finalMap, String fileID, int topK) throws Exception
	{
		String fileName = OUTPUT_FILE_PREFIX + fileID + 2 + OUTPUT_FILE_SUFFIX;
		File file = new File(PackageSummaryCrawler.MIDP_PKG_DMP_FILE);
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    @SuppressWarnings("unchecked")
		HashMap<String, String> pageMap = (HashMap<String, String>) s.readObject();
	    s.close();
	    
	    PrintWriter out = new PrintWriter(new File(fileName));
		List<String> lst;
		StringBuffer buff;
		Boolean flag = false;
		Set<String> tgtPkgSet, tgtPkgSetPerTopic; 
		Set<String> srcClassSet = pageMap.keySet();
		String tmp;
		for(String pkgStr: srcClassSet)
		{
			tgtPkgSet = new TreeSet<String>();
			out.println(pkgStr);
			for(String key: finalMap.keySet())
			{
				tgtPkgSetPerTopic = new TreeSet<String>();
				buff = new StringBuffer();
				flag = false;
				lst = finalMap.get(key);
				for (int i = 0; (i < lst.size() && i < topK); i++) 
				{
					tmp = lst.get(i).split("\t")[1];
					if(lst.get(i).endsWith(pkgStr))
					{
						flag = true;
						break;
					}
					tgtPkgSetPerTopic.add(tmp);
				}
				if(flag)
					tgtPkgSet.addAll(tgtPkgSetPerTopic);
			}
			
			for(String res: tgtPkgSet)
			{	
				if(!srcClassSet.contains(res))
					out.println("\t" + res);
			}
			
		}
		out.close();
	}
	
	//@Override
	protected void test1(Map<String, List<String>> finalMap, String fileID, int topK) {
		String fileName = OUTPUT_FILE_PREFIX + fileID + 1 + OUTPUT_FILE_SUFFIX;
		try 
		{
			PrintWriter out = new PrintWriter(new File(fileName));
			List<String> lst;
			StringBuffer buff;
			Boolean flag = false;
			for(Object key: finalMap.keySet())
			{
				buff = new StringBuffer();
				flag = false;
				buff.append(key);
				buff.append("\n");
				lst = finalMap.get(key);
				for (int i = 0; (i < lst.size() && i < topK); i++) {
					if(lst.get(i).endsWith("javax.microedition.lcdui"))
					{
						flag = true;
						break;
					}
					buff.append("\t" + lst.get(i) );
					buff.append("\n");
				}
				buff.append("_________________________________________________________________________________");
				buff.append("\n");
				if(flag)
					out.write(buff.toString());
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		super.writeToFile(finalMap, fileID, topK);
	}

}
