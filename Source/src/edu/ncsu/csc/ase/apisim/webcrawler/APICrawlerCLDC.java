package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.cache.FailedCache;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JarClassLoader;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JavaTypeDecorator;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * Class for Crawling elements from online J2ME html document.
 * @author Rahul Pandita
 *
 */
public class APICrawlerCLDC{

	private Document doc;
	
	private APIType clazz;
	
	public APIType processURL(String url, String typeName)
	{
		loadDoc(typeName, url);
		//Gets package Name
		clazz = new APIType(classPackageExtractor(typeName), typeName);
		clazz.setUrl(url);
		
		//Gets class description
		classDescriptionExtractor(typeName);
		
		//Gets Method Information
		classElementExtractor(typeName);
		
		// Type additions
		Class<?> javaClass = JarClassLoader.loadTypeSilently(clazz);
		if(javaClass!=null)
				JavaTypeDecorator.decorateType(javaClass, clazz);
		return clazz;
	}
	
	private void loadDoc(String entity,String url)
	{
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) 
		{
			System.err.println("Trying Again!\n"+ entity+"\t<"+url+">" );
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException ex) 
			{
				FailedCache.getInstance().addFailedURL(entity, url);
				ex.printStackTrace();
			}
		}
	}
	
	
	private String classPackageExtractor(String typeName)
	{
		Elements links = doc.getElementsByClass("header");
		for (Element link : links)
		{
			if(link.children().size()==2)
			{
				return StringUtil.cleanHTML(link.child(0).text());
			}
			else
			{
				System.out.println("here!!");
			}
		}
		return "";
	}
	
	private void classDescriptionExtractor(String javaClass) {
		StringBuffer buff = new StringBuffer();
		// doc = Jsoup.connect(url).get();
		Elements links = doc.select("dl");
		Element link1 = null;
		for (Element link : links) {
			// ASTBuilder.parseASTMethod(cleanHTML(link.text()));
			if (StringUtil.cleanHTML(link.text()).contains(javaClass)) {
				String regExp = ".*\\s+(interface|class)\\s+("
						+ javaClass
						+ ")(\\s+((extends\\s+\\w+( ,\\w+)*)))*\\s*(\\s+((implements\\s+\\w+( ,\\w+)*)))*\\s*";
				// String regExp =
				// ".*interface\\s+(\\w+)(\\s+extends\\s+(\\w+))?(\\s+implements\\s+(\\w|,)+)?$";
				Pattern pattern = Pattern.compile(regExp);
				// Pattern pattern = Pattern.compile(regExp, Pattern.DOTALL |
				// Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher match = pattern.matcher(StringUtil.cleanHTML(link.text()));
				if (match.find()) {
					buff.append(StringUtil.cleanHTML(link.text()));
					buff.append("\n\t");
					link1 = link.nextElementSibling();
					while (!link1.nodeName().equalsIgnoreCase("hr")) {
						buff.append(link1.text());
						buff.append("\n");
						link1 = link1.nextElementSibling();
					}
					break;
				}
			}
		}
		clazz.setSummary(StringUtil.cleanHTML(buff.toString()));
	}
	
	private void classElementExtractor(String javaClass)
	{
		StringBuffer buff;
		
		Elements links = doc.select("pre");
		Element link1 = null;
		for (Element link : links)
		{
			if(ASTBuilder.parseASTMethod(StringUtil.cleanHTML(link.text())))
			{
				APIMtd mtd = new APIMtd("", StringUtil.cleanHTML(link.text()));	
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
	}
	
}
