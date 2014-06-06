package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * This Class added programming elements to the an {@link APIType} Object
 * @author Rahul Pandita
 *
 */
public class JavaTypeDecorator {

	public static void decorateType(Class<?> javaClass, APIType apiType) {
		apiType.setTypeAnnotated(true);
		apiType.setInterfaze(javaClass.isInterface());
		apiType.setEnums(javaClass.isEnum());
		apiType.setModifier(String.valueOf(javaClass.getModifiers()));

		// ImplementedTypes
		Type[] directinterfaceList = javaClass.getGenericInterfaces();
		for (Type interfaze : directinterfaceList) 
		{
			String interfaceName = interfaze.toString().replace("interface ", "");
			if (interfaze instanceof ParameterizedType)
				interfaceName = ((ParameterizedType) interfaze).getRawType().toString().replace("interface ", "");
			apiType.addImplementsList(interfaceName);
		}
		//SuperClasses
		if ((javaClass.getSuperclass() != null) && (javaClass.getSuperclass().getCanonicalName() != null))
			apiType.addExtendsList(javaClass.getSuperclass().getCanonicalName());
		
		//Methods
		for (APIMtd mtd : apiType.getMethod()) {
			MethodDecorator(mtd, javaClass);
		}
		//Constructor
		for (APIMtd mtd : apiType.getConstructors()) {
			MethodDecorator(mtd, javaClass);
		}
	}
	
	private static void MethodDecorator(APIMtd mtd, Class<?> javaClass) 
	{
		try 
		{
			CompilationUnit cu;
			cu = JavaParser.parse(new ByteArrayInputStream(("public interface a {" + mtd.getName() + "{};}").getBytes()));
			ClassOrInterfaceDeclerationVisitor visitor = new ClassOrInterfaceDeclerationVisitor();
			visitor.javaClass = javaClass;
			visitor.apiMethod = mtd;
			cu.accept(visitor, new MethodDeclaration());
		} 
		catch(ParseException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Simple visitor implementation for visiting {@link ConstructorDeclaration} and {@link MethodDeclaration} nodes. 
     */
    private static class ClassOrInterfaceDeclerationVisitor extends VoidVisitorAdapter<Object> {
    	private Class<?> javaClass;
    	private APIMtd apiMethod;
        @Override
        public void visit(MethodDeclaration n, Object type) 
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
    						apiMethod.setTypeAnnotated(true);
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
        	
        }
        
        @Override
        public void visit(ConstructorDeclaration n, Object type){
        	for(Constructor<?> mtd: javaClass.getConstructors())
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
    						apiMethod.setTypeAnnotated(true);
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
