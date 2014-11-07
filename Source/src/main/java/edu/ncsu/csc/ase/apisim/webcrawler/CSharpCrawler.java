package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.ASTBuilder;

public class CSharpCrawler 
{
	private static final String HTML_CSS_TABLEDIV_CLASS = "tablediv";
	private static final String MSDN_MICROSOFT_LIBRARY_URL_PREFIX = "http://msdn.microsoft.com/en-us/library/";
	private static final String HTML_DL_TAG = "dl";
	private static final String HTML_A_HREF_ATTRIBUTE = "abs:href";
	private static final String HTML_ABS_HREF_TAG = "a[href]";
	public static final String DOT_NET_CLASS_LIBRARY_URL = "http://msdn.microsoft.com/en-us/library/d11h6832(v=vs.71).aspx";
	private static final String CONTENT = "content";
	
	
	private static HashMap<String, String> hmap = new HashMap<String, String>();
	
	public static void main(String[] args) {
		read();
		try
		{
		CSharpCrawler crawler = new CSharpCrawler();
		List<APIType> abc = crawler.crawl();
		AllClassCrawler.write(abc, "DONET.api");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		write();
	}
	
	public static void write() {
		try {
			FileOutputStream fos = new FileOutputStream("hashmap.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hmap);
			oos.close();
			fos.close();
			System.out.printf("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void read() {
		try {
			FileInputStream fis = new FileInputStream("hashmap.ser");
	        ObjectInputStream ois = new ObjectInputStream(fis);
	         hmap = (HashMap) ois.readObject();
	         ois.close();
	         fis.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	

	/**
	 * Entry Method to Crawl .NET Documentation from url in {@link #DOT_NET_CLASS_LIBRARY_URL}
	 * @return
	 */
	public List<APIType> crawl() {
		List<APIType> typeList = new ArrayList<APIType>();
		
		List<String> namespace_Url_List = getNamespaceUrlList(); 
		for(String url: namespace_Url_List)
		{
			//url = "http://msdn.microsoft.com/en-us/library/microsoft.csharp(v=vs.71).aspx";
			typeList.addAll(getTypeList(url));
		}
		return typeList;
	}

	private List<APIType> getTypeList(String url) {
		List<APIType> list = new ArrayList<APIType>();
		List<String> typUrlList = getTypeUrlList(url);
		APIType typ;
		for(String typeUrl:typUrlList)
		{
			typ = crawlAPIType(typeUrl);
			if(typ!=null)
				list.add(typ);
			
		}
		return list;
	}

	private APIType crawlAPIType(String typeUrl) {
		assert typeUrl!=null && typeUrl!="";
		Element ele = getContent(typeUrl,0);
		
		Element element = ele;
		try
		{
			element = ele.getElementsByClass("topic").first().getElementsByClass("title").first();
		}catch(Exception e)
		{
			return null;
		}
		APIType type = new APIType("", "");
		switch(element.text().split("\\s")[1].trim())
		{
			case "Enumeration":
				type.setEnums(true);
				break;
			case "Interface":
				type.setInterfaze(true);
			case "Method":
				return null;
			case "Event":
				return null;
			case "Delegate":
				return null;
			case "Field":
				return null;
			default:
				break;
		}
		
		type.setApiName(APITYPE.DOTNET.name());
		type.setName(element.text().split("\\s")[0].trim());
		type.setPackage(nameSpaceExtactor(typeUrl));
		
		if(!(type.isEnums()||type.isInterfaze()))	
		{
			String memberUrl = getMmeberUrl(ele, type.getName()+ " Members");
			assert memberUrl.length()>0;
			type.setSummary(extractDesc(ele, "For a list of all members of this type"));
			try{
			updateMembers(type, memberUrl);
			}
			catch(Exception e)
			{
				assert false;
			}
		}	
		return type;
	}

	private void updateMembers(APIType type, String memberUrl) {
		Element ele = getContent(memberUrl,0);
		Element element = ele.getElementById("nstext");
		Elements elements = element.getElementsByClass("dtH4");
		for(Element header: elements)
		{
			if(header.text().trim().equalsIgnoreCase("Public Methods"))
			{
				assert header.nextElementSibling().tag().equals("div");
				assert header.nextElementSibling().className().equalsIgnoreCase(HTML_CSS_TABLEDIV_CLASS);
				Elements mtdList = header.nextElementSibling().select(HTML_ABS_HREF_TAG);
				for(Element mtdEle: mtdList)
				{
					
					if((mtdEle.attr(HTML_A_HREF_ATTRIBUTE).toLowerCase()).contains((type.getPackage()+"."+type.getName()).toLowerCase()))
					{
						APIMtd mtd = crawlMethod(mtdEle.text().trim(), mtdEle.attr(HTML_A_HREF_ATTRIBUTE));
						mtd.setParentClass(type);
						type.addMethod(mtd);
					}
				}
				break;
			}
		}
		
	}
	private APIMtd crawlMethod(String mtdName, String url) {
		Element ele = getContent(url,0);
		APIMtd mtd = new APIMtd("", mtdName);
		mtd.setDescription(extractMtdDesc(ele));
		String syntaxStr = extractSyntaxStr(ele);
		//System.out.println(syntaxStr +"");
		ASTBuilder.decorateAPIMtd(syntaxStr, mtd);
		
		return mtd;
	}

	private String extractSyntaxStr(Element ele) {
		Element element = ele.getElementsByClass("syntax").first();
		if(element==null)
			return "";
		StringBuffer buff = new StringBuffer();
		boolean flag= false;
		for(Element e: element.children())
		{
			if(e.className().equalsIgnoreCase("lang"))
			{
				if (e.text().trim().equals("[C#]"))
				{
					flag = true;
					continue;
				}
				else if (e.text().trim().equals("[C++]"))
					break;
			}
			if(flag)
			{
				if(!e.text().startsWith("["))
					buff.append(" ");
				buff.append(e.text());
				
			}
		}
		String returnVal = buff.toString().trim();
		while(returnVal.contains("  "))
			returnVal = returnVal.replace("  ", " ");
		assert returnVal.endsWith(";");
		returnVal = returnVal.substring(0,returnVal.lastIndexOf(";"));
		return returnVal;
	}

	private String extractMtdDesc(Element ele) {
		StringBuffer buff = new StringBuffer();
		for(Element el:ele.getElementById("nstext").children())
		{
			if(el.text().trim().startsWith("[Visual"))
				continue;
			else if(el.text().trim().startsWith("Requirements"))
				break;
			else
				buff.append(el.text()).append("\n");
		}
		return buff.toString();
	}
	
	private String extractDesc(Element ele, String stopStr) {
		StringBuffer buff = new StringBuffer();
		for(Element el:ele.getElementById("nstext").children())
		{
			if(el.text().trim().startsWith(stopStr))
				break;
			
			buff.append(el.text()).append("\n");
		}
		return buff.toString();
	}

	private Element getContent(String url, int recursion) {
		Element element = null;
		try
		{
			Document doc;
			if(hmap.containsKey(url))
			{
				doc = Jsoup.parse(hmap.get(url));
				System.out.println("cache hit!");
			}
			else
			{
				System.out.println("cache miss!" + url);
				doc = Jsoup.connect(url).get();
				hmap.put(url, doc.html());
			
			}
			element = doc.getElementById(CONTENT);
		}
		catch(IOException e)
		{
			System.out.println("Error connecting to URL " + url);
			e.printStackTrace();
			try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(recursion <10)
				getContent(url, recursion+1);
		}
		assert element!=null;
		System.err.println("LAST URL Crawled " + url);
		return element;
	}
	
	private String getMmeberUrl(Element element, String name) {
		Elements elList = element.select(HTML_ABS_HREF_TAG);
		for(Element link: elList)
		{
			if(link.text().trim().equalsIgnoreCase(name))
			return link.attr(HTML_A_HREF_ATTRIBUTE);
		}
		return "";
	}

	private String nameSpaceExtactor(String typeUrl) {
		String namespace = typeUrl.replace(MSDN_MICROSOFT_LIBRARY_URL_PREFIX, "");
		assert namespace.contains("(");
		namespace = namespace.substring(0, namespace.indexOf("("));
		assert namespace.contains(".");
		try{
		namespace = namespace.substring(0, namespace.lastIndexOf("."));
		assert namespace.length()>0;
		}
		catch(Exception e)
		{
			namespace="";
		}
		return namespace.trim();
	}

	private List<String> getTypeUrlList(String url) {
		List<String> typeUrlList = new ArrayList<String>();
		
		Element ele = getContent(url,0);
		Elements elements =null;
		try
		{
			elements = ele.getElementsByClass(HTML_CSS_TABLEDIV_CLASS);
		}catch(Exception e)
		{
			System.out.println("here");
		}
		assert elements!=null;
		assert elements.size()>0;
		if(elements==null)
			return typeUrlList;
		for(Element t_ele:elements)
		{
			typeUrlList.addAll(getURLList(t_ele));
		}
		
		return typeUrlList;
	}

	private List<String> getNamespaceUrlList() 
	{
		List<String> namespaceUrlList = new ArrayList<String>();
		Element ele = getContent(DOT_NET_CLASS_LIBRARY_URL,0);
		ele = ele.getElementsByClass("dtH2").first().nextElementSibling().nextElementSibling();
		assert ele!=null;
		assert ele.tagName().equalsIgnoreCase(HTML_DL_TAG);
		
		namespaceUrlList.addAll(getURLList(ele));
		
		return namespaceUrlList;
	}

	private List<String> getURLList(Element element) {
		List<String> url_List = new ArrayList<String>();
		Elements elList = element.select(HTML_ABS_HREF_TAG);
		for(Element link: elList)
		{
			if(link.attr(HTML_A_HREF_ATTRIBUTE)!=null&&!link.attr(HTML_A_HREF_ATTRIBUTE).equals(""))
				url_List.add(link.attr(HTML_A_HREF_ATTRIBUTE));
		}
		return url_List;
	}
}
