package edu.ncsu.csc.ase.apisim.webcrawler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ncsu.csc.ase.apisim.cache.FailedCache;
import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

public class AllClassCrawlerTest 
{
	public void testCLDCWebGet()
	{
		List<APIType> classList = AllClassCrawler.listClassesCLDC(Configuration.CLDC_ALL_CLASS_URL);
		Assert.assertTrue(classList.size()==81);
	}
	
	@Test
	public void testCLDC()
	{
		AllClassCrawler.writeCLDC(Configuration.CLDC_ALL_CLASS_URL, Configuration.CLDC_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println("Finished CLDC");
		System.out.println("-----------------------------------------------------------");
		System.out.println(FailedCache.getInstance().toString());
	}
	
	@Test
	public void testMIDP()
	{
		AllClassCrawler.write(Configuration.MIDP_ALL_CLASS_URL, Configuration.MIDP_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println("Finished MIDP");
		System.out.println("-----------------------------------------------------------");
		System.out.println(FailedCache.getInstance().toString());
	}
	
	@Test
	public void testAndroid()
	{
		AllClassCrawler.writeAndroid(Configuration.ANDROID_ALL_CLASS_URL, Configuration.ANDROID_DUMP_PATH);
		System.out.println("-----------------------------------------------------------");
		System.out.println("Finished Android");
		System.out.println("-----------------------------------------------------------");
		System.out.println(FailedCache.getInstance().toString());
	}
	
}
