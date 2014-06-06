package edu.ncsu.csc.ase.apisim.webcrawler;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.dataStructure.APITypeFactory;
import edu.ncsu.csc.ase.dristi.util.ConsoleUtil;

public class ASTBuilder{
	
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
	
	public static boolean parseASTType (String htmlTest) {
		retValue = false;
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setSource((htmlTest+"{public void mn(String[] args);}").toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//ASTNode node = parser.createAST(null);
		
 
		try
		{
			cu = (CompilationUnit) parser.createAST(null);
			System.out.println(cu.types());
			for(Object obj: cu.types())
			{
				System.out.println(obj.getClass());
				if(obj instanceof TypeDeclaration)
				{
					TypeDeclaration typeDec = (TypeDeclaration)obj;
					System.out.println(typeDec.isInterface());
					for(Object obj1: typeDec.superInterfaceTypes())
					{
						System.out.println(obj1.getClass());
						if(obj1 instanceof TypeDeclaration)
						{
							System.out.println(((TypeDeclaration)obj1).getName().toString());
						}
						System.out.println(obj1);
						
					}
					System.out.println(typeDec.getSuperclassType());
					System.out.println();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	public static void main(String[] args) {
		main22("public abstract class LongBuffer extends Buffer implements Comparable<T>{}", new APIType("", "LongBuffer"));
	}
	
	public static void main22(String source, APIType var) {
		japa.parser.ast.CompilationUnit cu = new japa.parser.ast.CompilationUnit();
		try {
			
			cu = JavaParser.parse(new ByteArrayInputStream(source.getBytes()));
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("error parsing string =>" +  source);
			source = ConsoleUtil.readConsole("Enter correct String ->" );
			try{
				cu = JavaParser.parse(new ByteArrayInputStream(source.getBytes()));
			}
			catch (ParseException ex)
			{
				ex.printStackTrace();
			}
		}
		ClassOrInterfaceDeclerationVisitor visitor = new ClassOrInterfaceDeclerationVisitor();
		cu.accept(visitor,var);
		
		
		
	

	}
	
	 /**
     * Simple visitor implementation for visiting {@link ClassOrInterfaceDeclaration} nodes. 
     */
    private static class ClassOrInterfaceDeclerationVisitor extends VoidVisitorAdapter<APIType> {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, APIType type) {
        	type.setInterfaze(n.isInterface());
        	
        	type.setModifier(Modifier.toString(n.getModifiers()));
        		
        	
        	if (n.getExtends() != null) 
        	{
    			for (ClassOrInterfaceType c : n.getExtends()) 
    			{
    				APIType type11 = APITypeFactory.getInstance().getAPIType(c.getName());
    				type.addExtendsList(type11.getPackage()+"."+type11.getName());
    			}
    		}

    		if (n.getImplements() != null) 
    		{
    			for (ClassOrInterfaceType c : n.getImplements()) 
    			{
    				type.addImplementsList(APITypeFactory.getInstance().getAPIType(c.getName()).getPackage() 
    						+ "." + APITypeFactory.getInstance().getAPIType(c.getName()).getName());
    			}
    		}
    		
        }
        
        @Override
        public void visit(japa.parser.ast.body.MethodDeclaration n, APIType type) {
        	
        }
    }
	
}

