package edu.ncsu.csc.ase.apisim.webcrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

/**
 * Class for Crawling elements from online MIDP html document.
 * @author Rahul Pandita
 *
 */
public class APICrawlerMIDP extends APICrawler {

	public APICrawlerMIDP() {
		type =  APITYPE.MIDP;
	}
	
	@Override
	protected String classPackageExtractor(String typeName)
	{
		StringBuffer buff = new StringBuffer();
		Elements links = doc.select("h2");
		for (Element link : links)
		{
			if((StringUtil.cleanHTML(link.text()).contains(typeName))&&(link.children().size()==2)&&(link.child(0).tagName().equalsIgnoreCase("font")))
			{
				String regExp = "([a-zA-Z]\\w*)(\\.[a-zA-Z]\\w*)*";
				Pattern pattern = Pattern.compile(regExp);
				Matcher match = pattern.matcher(StringUtil.cleanHTML(link.child(0).text()));
				if (match.find())
				{
					return StringUtil.cleanHTML(link.child(0).text());
				}
			}
		}
		return StringUtil.cleanHTML(buff.toString());
	}
	
	@Override
	protected void classDescriptionExtractor(String javaClass) {
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
	
	@Override
	protected void classElementExtractor(String javaClass)
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
