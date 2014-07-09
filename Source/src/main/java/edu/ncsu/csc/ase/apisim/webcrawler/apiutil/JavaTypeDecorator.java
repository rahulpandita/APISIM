package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

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
			ASTBuilder.methodDecorator(mtd, javaClass);
		}
		//Constructor
		for (APIMtd mtd : apiType.getConstructors()) {
			ASTBuilder.methodDecorator(mtd, javaClass);
		}
	}
	
	public static void main(String[] args) {
		List<APIType> typeList = AllClassCrawler.read(Configuration.MIDP_DUMP_PATH);
		for(APIType type: typeList)
		{
			Class<?> clazz= JarClassLoader.loadTypeSilently(type);
			if(clazz !=null)
				decorateType(clazz,type);
			
		}
		AllClassCrawler.write(typeList, Configuration.MIDP_DUMP_PATH+"1");
	}
}
