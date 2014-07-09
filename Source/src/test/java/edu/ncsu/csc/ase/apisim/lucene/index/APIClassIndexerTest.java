package edu.ncsu.csc.ase.apisim.lucene.index;

import org.junit.Test;

/**
 * Test Class for {@link APIClassIndexer}
 * @author Rahul Pandita
 *
 */
public class APIClassIndexerTest {


	@Test
	public void testCreate() 
	{
		try 
		{
			APIClassIndexer idxr = new APIClassIndexer();
			idxr.rebuildIndexes();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
