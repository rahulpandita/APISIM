package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * This class is responsible to load the Classes from the jars included in ClassPath
 * @author Rahul Pandita
 *
 */
public class JarClassLoader 
{
	public static Class<?> loadTypeSilently(APIType apiclazz)
	{
		Class<?> clazz= null;
		try
		{
			clazz = loadType(apiclazz);
		}
		catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError e) 
		{
			e.printStackTrace();
		}
		return clazz;
	}
	
	public static Class<?> loadType(APIType apiclazz) throws ClassNotFoundException 
	{
		String clazzName = apiclazz.getName();
		Class<?> clazz;
		if(clazzName.contains("."))
		{
			clazzName = clazzName.substring(0,clazzName.indexOf("."));
			clazz = getInnerClass(
					Class.forName(apiclazz.getPackage()+"."+clazzName), 
					apiclazz.getPackage()+"."+apiclazz.getName());
		}
		else 
		{
			clazz = Class.forName(apiclazz.getPackage()+"."+apiclazz.getName());
		}
		return clazz;
	}
	
	public static Class<?> getInnerClass(Class<?> clazz, String name) throws ClassNotFoundException
	{
		String clsCanonicalName;
		for (Class<?> cls : clazz.getDeclaredClasses()) 
		{
			// You would like to exclude static nested classes 
			// since they require another approach.
			clsCanonicalName= cls.getCanonicalName();
			if(clsCanonicalName!=null)
			{
				if(cls.getCanonicalName().equals(name))
					return cls;
				else if(name.startsWith(cls.getCanonicalName()+ "."))
				{
					return getInnerClass(cls, name);
				}
			}
				
			
		}
		throw new ClassNotFoundException();
	}
}
