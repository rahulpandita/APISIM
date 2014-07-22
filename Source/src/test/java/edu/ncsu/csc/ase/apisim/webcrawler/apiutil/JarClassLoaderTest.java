package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;


/**
 * Test Class for {@link JarClassLoader}
 * @author Rahul Pandita
 *
 */
public class JarClassLoaderTest 
{

	//Intentional to test an old method for fall~back in future
	@SuppressWarnings("deprecation")
	@Test
	public void testLoadR()
	{
		try
		{
			Class<?> testClass = Class.forName("android.R");
			Class<?> clazz = JarClassLoader.getInnerClass(testClass, "android.R.xml");
			System.out.println(clazz.getCanonicalName());
			Assert.assertTrue("android.R.xml".equalsIgnoreCase(clazz.getCanonicalName()));
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testCLDC()
	{
		List<APIType> apiList = AllClassCrawler.read(Configuration.CLDC_DUMP_PATH);
		List<APIType> manualList = new ArrayList<APIType>();
		for(APIType apiclazz:apiList)
		{
			try 
			{
				JarClassLoader.loadType(apiclazz);
			} catch (ClassNotFoundException|NoClassDefFoundError|ExceptionInInitializerError|UnsatisfiedLinkError|IOException e) {
				manualList.add(apiclazz);
				
			}
		}
		Assert.assertTrue(manualList.size()==0);
	}

	@Test
	public void testMIDP()
	{
		List<APIType> apiList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		List<APIType> manualList = new ArrayList<APIType>();
		for(APIType apiclazz:apiList)
		{
			try 
			{
				JarClassLoader.loadType(apiclazz);
			} catch (ClassNotFoundException|NoClassDefFoundError|ExceptionInInitializerError|UnsatisfiedLinkError|IOException e) {
				manualList.add(apiclazz);
				
			}
		}
		System.out.println(manualList.size());
		Assert.assertTrue(manualList.size()==0);
	}
	
	
	
}
