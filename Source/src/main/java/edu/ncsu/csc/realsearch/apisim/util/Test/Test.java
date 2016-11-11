package edu.ncsu.csc.realsearch.apisim.util.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

public class Test {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		List<APIType> typeList = AllClassCrawler.read(Configuration.JAVA_DUMP_PATH);
		//typeList.addAll(AllClassCrawler.read(Configuration.DOTNET_DUMP_PATH));
		typeList.addAll(AllClassCrawler.read(Configuration.CLDC_DUMP_PATH));
		typeList.addAll(AllClassCrawler.read(Configuration.MIDP_DUMP_PATH));
		typeList.addAll(AllClassCrawler.read(Configuration.ECLIPSE_DUMP_PATH));
		typeList.addAll(AllClassCrawler.read(Configuration.ANDROID_DUMP_PATH));
		
		PrintWriter writer = new PrintWriter("tst3", "UTF-8");
		for(APIType typ: typeList)
		{
			
			writer.println(clean(typ.toString()));
			
		}
		writer.close();
	}
	
	public static String clean(String str)
	{
		StringBuffer buff = new StringBuffer();
		str = StringUtil.cleanHTML(str);
		for(String s : str.split("\n"))
		{
			buff.append(clean1(s)).append(" ");
		}
		
		return buff.toString().replaceAll("\t", " ").replaceAll(Pattern.quote("  "), " ");
	}
	public static String clean1(String str)
	{
		str = str.replaceAll(Pattern.quote("----------METHOD LIST---------"), "");
		str = str.replaceAll(Pattern.quote("----------SUMMARY---------"), "");
		str = str.replaceAll(Pattern.quote("-----------------------"), "");
		str = str.replaceAll(Pattern.quote("<code>"), "");
		str = str.replaceAll(Pattern.quote("</code>"), "");
		str = str.replaceAll(Pattern.quote("<tt>"), "");
		str = str.replaceAll(Pattern.quote("</tt>"), "");
		str = str.replaceAll(Pattern.quote("<td>"), "");
		str = str.replaceAll(Pattern.quote("</td>"), "");
		str = str.replaceAll(Pattern.quote("<tr>"), "");
		str = str.replaceAll(Pattern.quote("</tr>"), "");
		str = str.replaceAll(Pattern.quote("<li>"), "");
		str = str.replaceAll(Pattern.quote("</li>"), "");
		str = str.replaceAll(Pattern.quote("<ul>"), "");
		str = str.replaceAll(Pattern.quote("</ul>"), "");
		str = str.replaceAll(Pattern.quote("<pre>"), "");
		str = str.replaceAll(Pattern.quote("</pre>"), "");
		str = str.replaceAll(Pattern.quote("<p>"), "");
		str = str.replaceAll(Pattern.quote("</p>"), "");
		str = str.replaceAll(Pattern.quote("<CODE>"), "");
		str = str.replaceAll(Pattern.quote("</CODE>"), "");
		str = str.replaceAll(Pattern.quote("<P>"), "");
		str = str.replaceAll(Pattern.quote("</P>"), "");
		
		
		//str = StringUtil.splitCamelCase(str);
		str = str.replaceAll(Pattern.quote("  "), " ");
		return str;
	}

}
