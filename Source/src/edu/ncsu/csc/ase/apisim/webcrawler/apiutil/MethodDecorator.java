package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;

class MethodDecorator extends ASTVisitor
{
	private boolean retValue;
	
	private Class<?> javaClass;
	
	private APIMtd apiMethod;
	
	public boolean isRetValue() {
		return retValue;
	}

	public MethodDecorator(Class<?> javaClass, APIMtd apiMethod) {
		this.retValue = false;
		this.javaClass = javaClass;
		this.apiMethod = apiMethod;
	}
	
	@Override
	public boolean visit(MethodDeclaration n) {

    	for(Method mtd: javaClass.getDeclaredMethods())
		{
    		if(n.getName().equals(mtd.getName()))
			{
				List<?> paramList = new ArrayList<SingleVariableDeclaration>();
				if(n.parameters()!=null)
					paramList =n.parameters();
				
				if(paramList.size()==mtd.getParameterTypes().length)
				{
					LinkedHashMap<String, String> variableIDMap = new LinkedHashMap<>();
					for(Object obj: paramList)
					{
						SingleVariableDeclaration param = (SingleVariableDeclaration)obj;
						String name = param.getName().getFullyQualifiedName();
						//Clean type for parameters and arrays
						String type = param.getType().toString();
						int dimentions = param.getExtraDimensions();
						if(type.contains("<"))
							type = type.substring(0,type.indexOf("<"));
						for(int i= 0;i<dimentions;i++)
							type = type + "[]";
						variableIDMap.put(name, type);
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
						if(n.isConstructor())
							apiMethod.setReturnType(mtd.getDeclaringClass().getCanonicalName());
						else
							apiMethod.setReturnType(mtd.getReturnType().getCanonicalName());
						apiMethod.setModifier(Modifier.toString(mtd.getModifiers()));
					}
				}
			}
		}
		return false;
	}
}