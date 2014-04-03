package edu.ncsu.csc.ase.apisim.util;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class ASTBuilder {
	
	public static CompilationUnit cu;
	
	private static boolean retValue = false;
	public static boolean parseASTMethod (String htmlTest) {
		retValue = false;
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setSource(("public interface A {"+ htmlTest  + "{};}").toCharArray());
		//parser.setSource((htmlTest+"{}").toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//ASTNode node = parser.createAST(null);
		
 
		try
		{
			cu = (CompilationUnit) parser.createAST(null);
		}
		catch(Exception e)
		{
			return false;
		}
		
		cu.accept(new ASTVisitor() {
 
			Set names = new HashSet();
			
			public boolean visit(MethodDeclaration node)
			{
				
				retValue= true;
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				//System.out.println("Declaration of '"+node.toString()+"' at line"+cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue to avoid usage info
				
			}
			
 
		});
		
		return retValue;
	}

}
