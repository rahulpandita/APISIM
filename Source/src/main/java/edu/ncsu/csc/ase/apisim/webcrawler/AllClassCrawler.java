package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

public class AllClassCrawler {
	public static List<APIType> listClassesMIDP(String url) {
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.select("a");
			for (Element link : links) {
				try {
					classList.add(new APICrawlerMIDP().processURL(
							link.attr("abs:href"), link.text()));
					System.out.println("processed " + link.text() + " <"
							+ link.attr("abs:href") + ">");
				} catch (Exception e) {
					System.err.println(link);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classList;
	}

	public static List<APIType> listClassesCLDC(String url) {
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.select("a");
			for (Element link : links) {
				try {
					classList.add(new APICrawlerCLDC().processURL(
							link.attr("abs:href"), link.text()));
					System.out.println("processed " + link.text() + " <"
							+ link.attr("abs:href") + ">");
				} catch (Exception e) {
					System.err.println(link);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classList;
	}

	public static List<APIType> listClassesAndroid(String url) {
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.select("td");
			for (Element link : links) {

				if ((link.children().size() == 1)
						&& (link.child(0).tagName().equalsIgnoreCase("a"))) {
					Element link1 = link.child(0);
					if (StringUtil.cleanHTML(link.text()).equals(
							StringUtil.cleanHTML(link1.text()))) {
						classList.add(new APICrawlerAndroid()
								.processURL(link1.attr("abs:href"),
										link.text()));
						System.out.println("processed " + link.text() + " <"
								+ link1.attr("abs:href") + ">");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classList;
	}
	
	public static List<APIType> listClassesWPAPI(String url) {
		List<APIType> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.select("a");
			for (Element link : links) {
				try {
					classList.add(new APICrawlerMIDP().processURL(
							link.attr("abs:href"), link.text()));
					System.out.println("processed " + link.text() + " <"
							+ link.attr("abs:href") + ">");
				} catch (Exception e) {
					System.err.println(link);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classList;
	}
	
	public static void writeAndroid(String url, String file) {
		List<APIType> classList = listClassesAndroid(url);
		write(classList, file);
	}

	public static void writeMIDP(String url, String file) {
		List<APIType> classList = listClassesMIDP(url);
		write(classList, file);
	}

	public static void writeCLDC(String url, String file) {
		List<APIType> classList = listClassesCLDC(url);
		write(classList, file);
	}

	public static void write(List<APIType> typeList, String file) {
		System.err.println(typeList.size());
		try {
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			for (APIType clazz : typeList)
				oos.writeObject(clazz);
			oos.close();
			System.out.println("Done");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.err.println("Writen " + typeList.size() + " documents to file");
	}

	public static List<APIType> read(String file) {
		List<APIType> classList = new ArrayList<>();
		APIType clazz;
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			while (fin.available() > 0) {
				clazz = (APIType) ois.readObject();
				sanitize(clazz);
				classList.add(clazz);
			}
			ois.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.err.println("Read " + classList.size() + " documents from file");
		return classList;
	}

	private static void sanitize(APIType clazz) {
		if(clazz.getName()==null)
			clazz.setName("");
		if(clazz.getPackage()==null)
			clazz.setPackage("");
		if(clazz.getApiName()==null)
			clazz.setApiName("");
		if(clazz.getSummary()==null)
			clazz.setSummary("");
		
		
	}
}