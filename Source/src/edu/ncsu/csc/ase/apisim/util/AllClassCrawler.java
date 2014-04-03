package edu.ncsu.csc.ase.apisim.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.util.dataStructure.APIClass;

public class AllClassCrawler 
{
	public static ArrayList<APIClass> listClassesJ2ME(String url)
	{
		ArrayList<APIClass> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		
			Elements links = doc.select("a");
			for (Element link : links)
			{
				try
				{
					classList.add(APICrawler.processURL(link.attr("abs:href"), link.text()));	
				}
				catch(Exception e)
				{
					System.err.println(link);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return classList;	
	}
	
	public static ArrayList<APIClass> listClassesAndroid(String url)
	{
		ArrayList<APIClass> classList = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		
			Elements links = doc.select("td");
			for (Element link : links)
			{
				
				if((link.children().size()==1)&&(link.child(0).tagName().equalsIgnoreCase("a")))
				{
					Element link1 = link.child(0);
					if(APICrawler.cleanHTML(link.text()).equals(APICrawler.cleanHTML(link1.text())))
					{
						classList.add(APICrawler.processURLAndroid(link1.attr("abs:href"), link.text()));
						System.out.println("processed "+  link.text() + " <" + link1.attr("abs:href") + ">");
					}
				}
			}
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}	
		return classList;	
	}
	
	public static void main(String[] args) {
		//Connected Limited Device Configuration (CLDC) 
		//Mobile Information Device Profile (MIDP) 
		//write("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/allclasses-frame.html", "midp.api");
		//read(android_Dump);
		//listClassesAndroid("http://developer.android.com/reference/classes.html");
		writeAndroid(Configuration.ANDROID_ALL_CLASS_URL, Configuration.ANDROID_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println("Finished Android");
		System.out.println("-----------------------------------------------------------");
		write(Configuration.CLDC_ALL_CLASS_URL,Configuration.CLDC_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println("Finished CLDC");
		System.out.println("-----------------------------------------------------------");
		write(Configuration.MIDP_ALL_CLASS_URL,Configuration.MIDP_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println(FailedCache.getInstance().toString());
		
	}
	
	
	
	
	
	public static void writeAndroid(String url, String file) {
		ArrayList<APIClass> classList = listClassesAndroid(url);
		System.err.println(classList.size());
		try{
			 
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			for(APIClass clazz:classList)
				oos.writeObject(clazz);
			oos.close();
			System.out.println("Done");
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		
		System.err.println("Writen "+ classList.size() + " documents to file");
	}
	
	public static void write(String url, String file) {
		ArrayList<APIClass> classList = listClassesJ2ME(url);
		System.err.println(classList.size());
		try{
			 
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			for(APIClass clazz:classList)
				oos.writeObject(clazz);
			oos.close();
			System.out.println("Done");
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		
		System.err.println("Writen "+ classList.size() + " documents to file");
	}
	
	
	public static void write(ArrayList<APIClass> classList, String file) {
		System.err.println(classList.size());
		try{
			 
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			for(APIClass clazz:classList)
				oos.writeObject(clazz);
			oos.close();
			System.out.println("Done");
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		
		System.err.println("Writen "+ classList.size() + " documents to file");
	}
	public static ArrayList<APIClass> read(String file) {
		ArrayList<APIClass> classList = new ArrayList<>();
		APIClass clazz;
		 
		   try{
	 
			   FileInputStream fin = new FileInputStream(file);
			   ObjectInputStream ois = new ObjectInputStream(fin);
			   while(fin.available()>0)
			   {
				   clazz = (APIClass) ois.readObject();
				   classList.add(clazz);
				   
			   }
			   ois.close();
	 
			  
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
			  
		   }
		
		System.err.println("Read "+ classList.size() + " documents from file");
		return classList;
	}
}
