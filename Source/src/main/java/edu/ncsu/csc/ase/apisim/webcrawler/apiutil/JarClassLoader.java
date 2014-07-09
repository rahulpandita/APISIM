package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * This class is responsible to load the Classes from the jars included in ClassPath
 * @author Rahul Pandita
 *
 */
public class JarClassLoader 
{
	private static List<JarEntry> jarEntryList;
	
	private static URLClassLoader classLoader;
	
	public static Class<?> initialize1(String name) throws IOException, ClassNotFoundException
	{
		initialize();
		return getClass(name);
	}

	private static Class<?> getClass(String name) throws ClassNotFoundException 
	{
		for(JarEntry je: jarEntryList)
		{
        	if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.').trim();
            if(className.equals(name.trim()))
            {
            	Class<?> c = classLoader.loadClass(className);
            	return c;
            }
        }
		return null;
	}

	/**
	 * @throws Exception
	 */
	private static void initialize() throws IOException {
		if(jarEntryList==null)
		{
			try	{
				File[] files = new File(Configuration.API_LIBS_PATH).listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File directory, String fileName) {
						return fileName.endsWith(".jar");
					}
				});
				
				jarEntryList  = new ArrayList<JarEntry>();
				List<URL> urlList = new ArrayList<URL>();
				for(File file:files)
				{
					JarFile jarFile = new JarFile(file);
					jarEntryList.addAll(Collections.list(jarFile.entries()));
					urlList.add(new URL("jar:file:" + file.getPath() +"!/"));
					jarFile.close();
				}
	
				URL[] urls = urlList.toArray(new URL[0]);
		        classLoader = URLClassLoader.newInstance(urls);
			} catch(IOException e)
			{
				jarEntryList=null;
				classLoader=null;
				throw e;
			}
		}
	}
	
	public static Class<?> loadTypeSilently(APIType apiclazz)
	{
		Class<?> clazz= null;
		try
		{
			clazz = loadType(apiclazz);
		}
		catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError | IOException e) 
		{
			e.printStackTrace();
		}return clazz;
	}
	
	public static Class<?> loadType(APIType apiclazz) throws ClassNotFoundException, IOException 
	{
		String clazzName = apiclazz.getName();
		Class<?> clazz;
		if(clazzName.contains("."))
		{
			clazzName = clazzName.replace(".", "$").trim();
		}
		
		clazz = initialize1(apiclazz.getPackage()+"."+clazzName);
		return clazz;
	}
	
	
	@Deprecated
	public static Class<?> loadTypeOld(APIType apiclazz) throws ClassNotFoundException 
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
	@Deprecated
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
