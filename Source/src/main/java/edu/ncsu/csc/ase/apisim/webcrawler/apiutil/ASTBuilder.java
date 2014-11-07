package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;

public class ASTBuilder {

	public static boolean parseASTMethod(String methodDecStr) 
	{
		CompilationUnit cu = createCompilationUnit(methodDecStr);
		if(cu !=null)
		{
			MethodVerifier visitor = new MethodVerifier();
			cu.accept(visitor);
			return visitor.isRetValue();
		} 
		return false;
	}
	
	public static String getJavaMethodName(String methodDecStr) 
	{
		CompilationUnit cu = createCompilationUnit(methodDecStr);
		if(cu !=null)
		{
			MethodVerifier visitor = new MethodVerifier();
			cu.accept(visitor);
			return visitor.getMethodName();
		} 
		return "";
	}
	
	public static void decorateAPIMtd(String methodDecStr, APIMtd mtd)
	{
		if(methodDecStr=="")
			return;
		CompilationUnit cu = createCompilationUnit(methodDecStr);
		if(cu !=null)
		{
			MethodVerifier visitor = new MethodVerifier();
			visitor.mtd = mtd;
			cu.accept(visitor);
			System.out.println("here");
			
		} 
		
		
		
	}
	
	private static CompilationUnit createCompilationUnit(String methodDecStr)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setSource(("public interface A {" + methodDecStr + "{};}").toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = null;
		try
		{
			cu = (CompilationUnit) parser.createAST(null);
		}	
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return cu;	
	}
	
	public static void methodDecorator(APIMtd mtd, Class<?> javaClass) 
	{
		CompilationUnit cu = createCompilationUnit(mtd.getName());
		if(cu !=null)
		{
			MethodDecorator visitor = new MethodDecorator(javaClass, mtd);
			cu.accept(visitor);
		} 
	}
}
