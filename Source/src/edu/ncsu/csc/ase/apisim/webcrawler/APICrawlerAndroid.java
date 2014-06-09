package edu.ncsu.csc.ase.apisim.webcrawler;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

/**
 * Class responsible to Crawl elements from online Android API html document
 * @author Rahul Pandita
 *
 */
public class APICrawlerAndroid extends APICrawler 
{
	public APICrawlerAndroid() {
		type = APITYPE.ANDROID;
	}
	
	public static void main(String[] args) {
		new APICrawlerAndroid().processURL("http://developer.android.com/reference/java/sql/Statement.html", "Statement");
	}

	@Override
	protected String classPackageExtractor(String typeName) {
		Element link1 = doc.getElementById("jd-content").child(0).child(0);
		link1 = link1.children().last().children().last();
		System.out.println(link1.text());
		String pkg = StringUtil.cleanHTML(link1.text());
		pkg = pkg.replace("."+typeName, "");
		return StringUtil.cleanHTML(pkg);
	}

	@Override
	protected void classDescriptionExtractor(String javaClass) {
		StringBuffer buff = new StringBuffer();
		Element link1 = doc.getElementById("jd-content");
		for(Element lnk: link1.children()) {
			if(!lnk.className().equalsIgnoreCase("jd-descr"))
				continue;
			else {
				buff.append(lnk.text());
				buff.append("\n");
				break;
			}
		}
		clazz.setSummary(StringUtil.cleanHTML(buff.toString()));
	}

	@Override
	protected void classElementExtractor(String javaClass) {
		StringBuffer buff;
		Elements links = doc.select("h4");
		Element link1 = null;
		for (Element link : links)
		{
			if(ASTBuilder.parseASTMethod(StringUtil.cleanHTML(link.text())))
			{
				APIMtd mtd = new APIMtd("", StringUtil.cleanHTML(link.text()));	
				buff = new StringBuffer();
				link1 = link.nextElementSibling().nextElementSibling();
				buff.append(StringUtil.cleanHTML(link1.text()));
				mtd.setParentClass(clazz);
				
				mtd.setDescription(buff.toString());
				
				clazz.addMethod(mtd);
			}
		}
	}
	
	
}
