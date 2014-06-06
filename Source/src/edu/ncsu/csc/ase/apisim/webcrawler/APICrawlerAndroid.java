package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.cache.FailedCache;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JarClassLoader;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JavaTypeDecorator;

/**
 * Class responsible to Crawl elements from online Android API html document
 * @author Rahul Pandita
 *
 */
public class APICrawlerAndroid 
{
	private Document doc;
	
	private APIType clazz;
	
	public APIType processURLAndroid(String url, String typeName)
	{
		loadDoc(typeName, url);
		clazz = new APIType(classPackageExtractorAndroid(typeName), typeName);
		clazz.setUrl(url);
		
		typeDescriptionExtractor();
		typeElementExtractor();
		
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
		} catch (IOException e) {
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
	
	private String classPackageExtractorAndroid(String typeName) {
		Element link1 = doc.getElementById("jd-content").child(0).child(0);
		link1 = link1.children().last().children().last();
		System.out.println(link1.text());
		String pkg = StringUtil.cleanHTML(link1.text());
		pkg = pkg.replace("."+typeName, "");
		return StringUtil.cleanHTML(pkg);
	}
	
	private void typeDescriptionExtractor() {
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
	
	private void typeElementExtractor()
	{
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
	
	public static void main(String[] args) {
		new APICrawlerAndroid().processURLAndroid("http://developer.android.com/reference/java/sql/Statement.html", "Statement");
	}
	
	
}
