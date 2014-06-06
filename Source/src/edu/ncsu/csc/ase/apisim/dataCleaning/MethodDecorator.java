package edu.ncsu.csc.ase.apisim.dataCleaning;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;
import edu.ncsu.csc.ase.apisim.webcrawler.apiutil.JarClassLoader;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;

public class MethodDecorator 
{
	private static String filepath = Configuration.ANDROID_DUMP_PATH; 
	
	public static void main(String[] args) 
	{
		List<APIType> clazzList = loadClassList();
		List<APIType> updatedClazz = new ArrayList<APIType>();
		for(APIType clazz:clazzList)
		{
			updatedClazz.add(updateMtds(clazz));
		}
		
		storeMethods(updatedClazz);
	}

	public static void main11(String[] args) {
		MethodDeclaration dec = parseASTMethod("public ApplicationInfo (ApplicationInfo orig)");
	}
	
	private static APIType updateMtds(APIType newClazz) {
		Class<?> javaClazz = classLoader(newClazz);
		if(javaClazz!=null)
		{	
			for (APIMtd mtd : newClazz.getMethod()) {
				mtdLoader(mtd, javaClazz);
			}
			for (APIMtd mtd : newClazz.getConstructors()) {
				mtdLoader(mtd, javaClazz);
			}
		}
		return newClazz;
	}
	
	
	private static Class<?> classLoader(APIType clazz) {
		Class<?> javaClass = null;
		try {
			javaClass = JarClassLoader.loadType(clazz);
		} catch (ClassNotFoundException | NoClassDefFoundError
				| ExceptionInInitializerError | UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
		return javaClass;
	}

	private static void mtdLoader(APIMtd mtd, Class<?> javaClass) 
	{
		try 
		{
			CompilationUnit cu;
			cu = JavaParser.parse(new ByteArrayInputStream(("public interface a {" + mtd.getName() + "{};}").getBytes()));
			ClassOrInterfaceDeclerationVisitor visitor = new ClassOrInterfaceDeclerationVisitor();
			visitor.javaClass = javaClass;
			visitor.apiMethod = mtd;
			cu.accept(visitor, new MethodDeclaration());
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static MethodDeclaration parseASTMethod(String source) {
		MethodDeclaration mtdDecAST = new MethodDeclaration();
		try {
			Class<?> javaClass = Class.forName("android.content.pm.ApplicationInfo");
		
		
			CompilationUnit cu;
		
			cu = JavaParser.parse(new ByteArrayInputStream(
					("public interface a {" + source + "{};}").getBytes()));
			ClassOrInterfaceDeclerationVisitor visitor = new ClassOrInterfaceDeclerationVisitor();
			visitor.javaClass = javaClass;
			cu.accept(visitor, mtdDecAST);
		} catch(ClassNotFoundException | NoClassDefFoundError
				| ExceptionInInitializerError | UnsatisfiedLinkError
				| ParseException e) {
			e.printStackTrace();
		}
		return mtdDecAST;
	}
	
	private static List<APIType> loadClassList() {
		// TODO Auto-generated method stub
		return AllClassCrawler.read(filepath);
	}
	
	public static void storeMethods(List<APIType> classList)
	{
		System.err.println(classList.size());
		try{
			 
			FileOutputStream fout = new FileOutputStream(filepath+"111");
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			for(APIType clazz:classList)
				oos.writeObject(clazz);
			oos.close();
			System.out.println("Done");
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		
		System.err.println("Writen "+ classList.size() + " documents to file");
	}
	
	/**
     * Simple visitor implementation for visiting {@link ClassOrInterfaceDeclaration} nodes. 
     */
    private static class ClassOrInterfaceDeclerationVisitor extends VoidVisitorAdapter<MethodDeclaration> {
    	private Class<?> javaClass;
    	private APIMtd apiMethod;
        @Override
        public void visit(MethodDeclaration n, MethodDeclaration type) 
        {
        	for(Method mtd: javaClass.getDeclaredMethods())
    		{
        		if(n.getName().equals(mtd.getName()))
    			{
    				List<Parameter> paramList = new ArrayList<Parameter>();
    				if(n.getParameters()!=null)
    					paramList = n.getParameters();
    				
    				if(paramList.size()==mtd.getParameterTypes().length)
    				{
    					LinkedHashMap<String, String> variableIDMap = new LinkedHashMap<>();
    					for(Parameter param: paramList)
    					{
    						variableIDMap.put(param.getType().toString(),param.getId().getName());
    					}
    					int i=0;
    					boolean val = true;
    					for(String paramType:variableIDMap.keySet())
    					{
    						if(mtd.getParameterTypes()[i].getCanonicalName().endsWith(paramType))
    						{
    							i++;
    						}
    						else
    						{
    							val = false;
    							break;
    						}
    					}
    					if(val)
    					{
    						for(Class<?> exceptionType:mtd.getExceptionTypes())
    						{
    							apiMethod.addException(exceptionType.getCanonicalName());
    						}
    						
    						for(Class<?> paramType:mtd.getParameterTypes())
    						{
    							apiMethod.addParameter(paramType.getCanonicalName());
    						}
    						
    						apiMethod.setReturnType(mtd.getReturnType().getCanonicalName());
    						apiMethod.setModifier(Modifier.toString(mtd.getModifiers()));
    					}
    				}
    			}
    		}
        	
        	type = n;
        }
        
        @Override
        public void visit(ConstructorDeclaration n, MethodDeclaration type){
        	for(Constructor mtd: javaClass.getConstructors())
    		{
        		//constructor;
        		if(n.getName().equals(mtd.getName().substring(mtd.getName().lastIndexOf(".")+1)))
    			{
    				List<Parameter> paramList = new ArrayList<Parameter>();
    				if(n.getParameters()!=null)
    					paramList = n.getParameters();
    				
    				if(paramList.size()==mtd.getParameterTypes().length)
    				{
    					LinkedHashMap<String, String> variableIDMap = new LinkedHashMap<>();
    					for(Parameter param: paramList)
    					{
    						variableIDMap.put(param.getType().toString(),param.getId().getName());
    					}
    					int i=0;
    					boolean val = true;
    					for(String paramType:variableIDMap.keySet())
    					{
    						if(mtd.getParameterTypes()[i].getCanonicalName().endsWith(paramType))
    						{
    							i++;
    						}
    						else
    						{
    							val = false;
    							break;
    						}
    					}
    					if(val)
    					{
    						for(Class<?> exceptionType:mtd.getExceptionTypes())
    						{
    							apiMethod.addException(exceptionType.getCanonicalName());
    						}
    						
    						for(Class<?> paramType:mtd.getParameterTypes())
    						{
    							apiMethod.addParameter(paramType.getCanonicalName());
    						}
    						apiMethod.setReturnType(mtd.getDeclaringClass().getCanonicalName());
    						apiMethod.setModifier(Modifier.toString(mtd.getModifiers()));
    					}
    				}
    			}
    			
    		}
        }
    }
}
