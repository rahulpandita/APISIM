package edu.ncsu.csc.ase.apisim.webcrawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.ncsu.csc.ase.apisim.cache.FailedCache;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JarClassLoader;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JavaTypeDecorator;

public abstract class APICrawler {

	protected Document doc;
	protected APIType clazz;
	protected APITYPE type;

	/**
	 * The main method that orchestrates the crawling of webpage of an API 
	 * @param url url of the API Class
	 * @param typeName Name of the API Type
	 * @return 
	 */
	public APIType processURL(String url, String typeName) {
		loadDoc(typeName, url);
		// Gets package Name
		clazz = new APIType(classPackageExtractor(typeName), typeName);
		clazz.setUrl(url);
		if(type == null)
			type = APITYPE.UNKNOWN;
		
		clazz.setApiName(type.name());

		// Gets class description
		classDescriptionExtractor(typeName);

		// Gets Method Information
		classElementExtractor(typeName);

		// Type additions
		Class<?> javaClass = JarClassLoader.loadTypeSilently(clazz);
		if (javaClass != null)
			JavaTypeDecorator.decorateType(javaClass, clazz);

		return clazz;
	}

	/**
	 * This method loads the webpage into the {@link #doc} variable.
	 * @param apiTypeName name of the API Type (class, enum, or interface)
	 * @param url The url to be crawled
	 */
	public void loadDoc(String apiType, String url) {
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			System.err.println("Trying Again!\n" + apiType + "\t<" + url + ">");
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException ex) {
				FailedCache.getInstance().addFailedURL(apiType, url);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Method for extracting the name of Package the APIType belongs to.
	 * @param  apiTypeName name of the API Type (class, enum, or interface)
	 * @return
	 */
	protected abstract String classPackageExtractor(String apiTypeName);

	/**
	 * Method for extracting the summary comments of the API type from the webpage
	 * @param apiTypeName name of the API Type (class, enum, or interface)
	 */
	protected abstract void classDescriptionExtractor(String apiTypeName);

	/**
	 * Method for extracting the type elements such as {@link APIMtd} of the API type from the webpage
	 * @param apiTypeName name of the API Type (class, enum, or interface)
	 */
	protected abstract void classElementExtractor(String apiTypeName);
}