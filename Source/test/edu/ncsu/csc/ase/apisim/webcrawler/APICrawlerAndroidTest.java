package edu.ncsu.csc.ase.apisim.webcrawler;

import org.junit.Assert;
import org.junit.Test;

import edu.ncsu.csc.ase.apisim.dataStructure.APIType;


/**
 * Class for testing {@link APICrawlerAndroid}
 * @author Rahul Pandita
 *
 */
public class APICrawlerAndroidTest 
{
	@Test
	public void testAbstractExecutorService ()
	{
		APICrawlerAndroid crawler = new APICrawlerAndroid();
		APIType typ = crawler.processURL("http://developer.android.com/reference/java/util/concurrent/AbstractExecutorService.html", "AbstractExecutorService");
		Assert.assertNotNull(typ);
	}
}
