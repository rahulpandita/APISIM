package edu.ncsu.csc.ase.apisim.eval.oracle;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test Class for {@link RosettaOracle}
 * 
 * @author Rahul Pandita
 *
 */
public class RosettaComparisonTest 
{
	
	
	@Test
	public void testRead() 
	{
		try
		{
			Assert.assertTrue("Test for reading Rossetta Results", RosettaOracle.read().keySet().size()==57);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Assert.assertFalse("Exception Occured in reading from XLS sheet",true);
		}
	}
}
