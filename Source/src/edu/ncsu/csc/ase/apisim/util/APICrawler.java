package edu.ncsu.csc.ase.apisim.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIMethod;

public class APICrawler {

	public static Document doc;
	
	public static void loadDoc(String entity,String url)
	{
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			FailedCache.getInstance().addFailedURL(entity, url);
			e.printStackTrace();
		}
	}
	
	public static String classPacakageExtractorJ2ME(String javaClass)
	{
		StringBuffer buff = new StringBuffer();
		Elements links = doc.select("h2");
		for (Element link : links)
		{
			if((cleanHTML(link.text()).contains(javaClass))&&(link.children().size()==2)&&(link.child(0).tagName().equalsIgnoreCase("font")))
			{
				String regExp = "([a-zA-Z]\\w*)(\\.[a-zA-Z]\\w*)*";
				Pattern pattern = Pattern.compile(regExp);
				Matcher match = pattern.matcher(cleanHTML(link.child(0).text()));
				if (match.find())
				{
					return cleanHTML(link.child(0).text());
				}
			}
					
		}
		System.out.println(buff.toString());
		return cleanHTML(buff.toString());
	}
	
	public static String classDescriptionExtractorJ2ME(String javaClass)
	{
		StringBuffer buff = new StringBuffer();
		//doc = Jsoup.connect(url).get();
			Elements links = doc.select("dl");
			Element link1 = null;
			for (Element link : links)
			{
				//ASTBuilder.parseASTMethod(cleanHTML(link.text()));
				if(cleanHTML(link.text()).contains(javaClass))
				{
					String regExp = ".*\\s+(interface|class)\\s+("+javaClass+")(\\s+((extends\\s+\\w+( ,\\w+)*)))*\\s*(\\s+((implements\\s+\\w+( ,\\w+)*)))*\\s*";
					//String regExp = ".*interface\\s+(\\w+)(\\s+extends\\s+(\\w+))?(\\s+implements\\s+(\\w|,)+)?$";
					Pattern pattern = Pattern.compile(regExp);
					//Pattern pattern = Pattern.compile(regExp, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
					Matcher match = pattern.matcher(cleanHTML(link.text()));
					if (match.find())
					{
						buff.append(cleanHTML(link.text()));
						buff.append("\n\t");
						link1 = link.nextElementSibling();
						while(!link1.nodeName().equalsIgnoreCase("hr"))
						{
							buff.append(link1.text());
							buff.append("\n");
							link1 = link1.nextElementSibling();
						}
						break;
					}
				}
					
			}
			
			
			
		return cleanHTML(buff.toString());
	}
	
	public static String classDescriptionExtractorAndroid(String javaClass) {
		StringBuffer buff = new StringBuffer();
		Element link1 = doc.getElementById("jd-content");
		for(Element lnk: link1.children())
		{
			if(!lnk.className().equalsIgnoreCase("jd-descr"))
				continue;
			else
			{
				buff.append(lnk.text());
				buff.append("\n");
				break;
			}
		}
		
		return cleanHTML(buff.toString());
	}
	
	public static String classPackageExtractorAndroid(String javaClass) {
		Element link1 = doc.getElementById("jd-content").child(0).child(0);
		
		link1 = link1.children().last().children().last();
		String pkg = cleanHTML(link1.text());
		pkg = pkg.replace("."+javaClass, "");
		return cleanHTML(pkg);
	}
	
	public static APIClass ClassElementExtractorAndroid(String javaClass, APIClass clazz)
	{
		StringBuffer buff;
		
		Elements links = doc.select("h4");
		Element link1 = null;
		for (Element link : links)
		{
			if(ASTBuilder.parseASTMethod(cleanHTML(link.text())))
			{
				APIMethod mtd = new APIMethod("", cleanHTML(link.text()));	
				buff = new StringBuffer();
				link1 = link.nextElementSibling().nextElementSibling();
				buff.append(cleanHTML(link1.text()));
				mtd.setParentClass(clazz);
				
				mtd.setDescription(buff.toString());
				
				clazz.addMethod(mtd);
			}
		}
			
		return clazz;
	}
	public static String classElementExtractorJ2ME(String javaClass)
	{
			StringBuffer buff = new StringBuffer();
			//doc = Jsoup.connect(url).get();
			Elements links = doc.select("pre");
			Element link1 = null;
			for (Element link : links)
			{
				//ASTBuilder.parseASTMethod(cleanHTML(link.text()));
				if(ASTBuilder.parseASTMethod(cleanHTML(link.text())))
				{
					buff.append("$$" + cleanHTML(link.text()));
					buff.append("\n\t");
					link1 = link.nextElementSibling();
					while(link1 != null && !link1.nodeName().equalsIgnoreCase("hr"))
					{
						if((link1.text().equalsIgnoreCase("Method Detail"))&&(link1.tagName().equalsIgnoreCase("table")))
							break;
						buff.append(link1.text());
						buff.append("\n");
						link1 = link1.nextElementSibling();
					}
					buff.append("\n\n");
				}
			}
			
			
			
		return cleanHTML(buff.toString());
	}
	
	public static APIClass ClassElementExtractorJ2ME(String javaClass, APIClass clazz)
	{
		StringBuffer buff;
		
		Elements links = doc.select("pre");
		Element link1 = null;
		for (Element link : links)
		{
			if(ASTBuilder.parseASTMethod(cleanHTML(link.text())))
			{
				APIMethod mtd = new APIMethod("", cleanHTML(link.text()));	
				buff = new StringBuffer();
				link1 = link.nextElementSibling();
				mtd.setParentClass(clazz);
				while(link1 != null && !link1.nodeName().equalsIgnoreCase("hr"))
				{
					if((link1.text().equalsIgnoreCase("Method Detail"))&&(link1.tagName().equalsIgnoreCase("table")))
						break;
					buff.append(link1.text());
					buff.append("\n");
					link1 = link1.nextElementSibling();
				}
				mtd.setDescription(buff.toString());
				clazz.addMethod(mtd);
			}
		}
			
		return clazz;
	}
	
	public static String cleanHTML(String ss) {
		ss = ss.replaceAll("\n", " ");
		ss = ss.replaceAll("\t", " ");
		while(!ss.equals(ss.replace("\u00a0", " ")))
			ss = ss.replace("\u00a0", " ");
		ss = ss.replaceAll("\\s+", " ");
		return ss.trim();
	}
	
	public static void main(String[] args) {
		processURLAndroid("http://developer.android.com/reference/java/sql/Statement.html", "Statement");
		
		//loadDoc("http://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/javax/microedition/io/Connector.html");
		//ClassDescriptionExtractor("http://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/javax/microedition/io/Connector.html","public class Connector");
		//classDescriptionExtractorAndroid("Character");
		//classPacakageExtractorJ2ME("Connector");
		
		
		//classElementExtractorJ2ME("Character");
		//ClassElementExtractor("^public static Connection open\\(String \\w+, int \\w+, boolean \\w+\\) throws IOException$");
		//ClassElementExtractor("http://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/java/lang/ArithmeticException.html","public ArithmeticException(String s)");
		//"http://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/"
		
		
	}
	public static APIClass processURL(String url, String entity)
	{
		loadDoc(entity, url);
		APIClass clazz = new APIClass(classPacakageExtractorJ2ME(entity), entity);
		clazz.setSummary(classDescriptionExtractorJ2ME(entity));
		
		clazz = ClassElementExtractorJ2ME(entity,clazz);
		return clazz;
	}
	
	public static APIClass processURLAndroid(String url, String entity)
	{
		loadDoc(entity, url);
		APIClass clazz = new APIClass(classPackageExtractorAndroid(entity), entity);
		clazz.setSummary(classDescriptionExtractorAndroid(entity));
		
		clazz = ClassElementExtractorAndroid(entity,clazz);
		//System.out.println(clazz.toString());
		return clazz;
	}
}
