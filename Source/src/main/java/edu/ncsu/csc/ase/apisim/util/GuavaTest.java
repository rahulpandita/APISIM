package edu.ncsu.csc.ase.apisim.util;

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

import com.google.common.reflect.ClassPath;


public class GuavaTest {
	public static void main1() throws IOException {
		ClassPath classPath = ClassPath.from(GuavaTest.class.getClassLoader());
		for (ClassPath.ClassInfo classInfo : classPath
				.getTopLevelClassesRecursive("javax.microedition")) {
			@SuppressWarnings("unused")
			Class<?> c = classInfo.load();
			/*System.err.println(c.toString());
			for (Method f : c.getMethods()) {
				System.out.println("\t" + f.toGenericString());
				
			}*/
		}
		System.err.println("here");

	}

	public static void dum15() {
		System.out.println("here!!!!");
	}
	
	public static void main(String[] args) throws Exception {
		test1();
	}
	
	private static void test1() throws Exception {
		//List<String> results = new ArrayList<>();
		File[] files = new File("libs/API/").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(".jar");
			}
		});
		
		List<JarEntry> e  = new ArrayList<JarEntry>();
		List<URL> urlList = new ArrayList<URL>();
		for(File file:files)
		{
			JarFile jarFile = new JarFile(file);
			e.addAll(Collections.list(jarFile.entries()));
			urlList.add(new URL("jar:file:" + file.getPath() +"!/"));
			jarFile.close();
			
		}

		URL[] urls = urlList.toArray(new URL[0]);
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        for(JarEntry je: e){
            
        	if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            if(className.equals("android.support.v7.app.ActionBar$LayoutParams"))
            {
            	//System.out.println("here");
            	System.out.println(className);
            	Class<?> c = cl.loadClass(className);
            	//System.out.println(c.getClasses());
            	for(Class<?> clz: c.getClasses())
            		System.out.println(clz.getName());
            	
            }
        }
	}
	
	

	
}
