package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import org.junit.Assert;
import org.junit.Test;

public class ASTBuilderTest {

	@Test
	public void test01() 
	{
		Assert.assertTrue(ASTBuilder.parseASTMethod("public A (Context c, AttributeSet attrs)"));
	}
	
	@Test
	public void test02() 
	{
		Assert.assertTrue(ASTBuilder.parseASTMethod("public static void main (String[] args)"));
	}
	
	@Test
	public void test03() 
	{
		Assert.assertTrue(ASTBuilder.parseASTMethod("public void abc.test03()"));
	}
	
	@Test
	public void test04()
	{
		Assert.assertTrue(ASTBuilder.parseASTMethod("public List<Future<T>> invokeAll (Collection<? extends Callable<T>> tasks)"));
	}
	
	@Test
	public void test05() 
	{
		Assert.assertFalse(ASTBuilder.parseASTMethod("public static{ void main (String[] args)"));
	}
	
	@Test
	public void test06() 
	{
		Assert.assertFalse(ASTBuilder.parseASTMethod("public void<> abc.test03()"));
	}
	
	@Test
	public void test07()
	{
		Assert.assertFalse(ASTBuilder.parseASTMethod("public List<Future<T> invokeAll (Collection<? extends Callable<T>> tasks)"));
	}
	
	@Test
	public void test08() 
	{
		Assert.assertFalse(ASTBuilder.parseASTMethod("public A (Context c), AttributeSet attrs)"));
	}
	
}
