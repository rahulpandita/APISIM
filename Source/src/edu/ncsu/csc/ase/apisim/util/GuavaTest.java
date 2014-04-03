package edu.ncsu.csc.ase.apisim.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.microedition.io.Connector;

import com.google.common.reflect.ClassPath;

public class GuavaTest {
	public static void main(String[] args) throws IOException {
		ClassPath classPath = ClassPath.from(GuavaTest.class.getClassLoader());
		for (ClassPath.ClassInfo classInfo : classPath
				.getTopLevelClassesRecursive("javax.microedition")) {
			Class<?> c = classInfo.load();
			System.err.println(c.toString());
			for (Method f : c.getMethods()) {
				System.out.println("\t" + f.toGenericString());
				
			}
		}
		System.err.println("here");

	}

	public static void dum15(Connector connector1) {
		System.out.println("here!!!!");
	}
}
