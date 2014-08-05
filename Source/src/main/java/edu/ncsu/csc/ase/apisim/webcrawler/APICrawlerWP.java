package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

public class APICrawlerWP 
{
	
	
	public static List<APIType> listClassesWPAPI(String url) {
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try 
		{
			doc = Jsoup.connect(url).get();
			Element sectionBlock = doc.select("div.sectionblock").first();
			System.out.println(sectionBlock);
			Elements links = sectionBlock.select("a");
			for (Element link : links) {
				try 
				{
					processNameSpace(link.text(),link.attr("abs:href"));
					System.out.println("processed " + link.text() + " <"+ link.attr("abs:href") + ">");
				} catch (Exception e) {
					System.err.println(link);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classList;
	}
	
	private static void processNameSpace(String name, String url) {
		if((name==null)||(name.equals(""))||(url==null)||(url.equals("")))
			return;
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try 
		{
			doc = Jsoup.connect(url).get();
			Elements sectionBlocks = doc.select("div.sectionblock");
			if(sectionBlocks.size()>2)
			{
				for(Element element: sectionBlocks)
				{
					Element table = element.getElementsByTag("table").first();
					if(table!=null)
					{
						Elements rows = table.getElementsByTag("tr");
						System.out.println(rows.first().text().trim().replace("Description", ""));
						for(Element row : rows) 
						{
							String Test = row.getElementsByTag("td").get(0).text();
							String Result = row.getElementsByTag("td").get(1).text();
							String Credit = row.getElementsByTag("td").get(2).text();
						}
					}
				}
					
				System.out.println("HERE");
			}
			Element ele = sectionBlocks.get(0).select("#typeList").first();
			System.out.println(ele);
			
			Element ele1 = sectionBlocks.get(1).select("#typeList").first();
			System.out.println(ele1);
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		listClassesWPAPI(Configuration.WP_NAMESPACE_URL);
	}
}
