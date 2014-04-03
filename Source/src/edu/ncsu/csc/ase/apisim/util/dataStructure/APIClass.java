package edu.ncsu.csc.ase.apisim.util.dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class APIClass implements Serializable 
{
	private String name;
	
	private String modifier;
	
	private APIClass parentClass;
	
	private List<APIField> fieldList;
	
	private List<APIConstructor> constructorList;
	
	private List<APIMethod> methodList;
	
	private String summary;
	
	private String pkg;
	
	@Override
	public String toString() {
		StringBuffer sbr = new StringBuffer();
		sbr.append(pkg);
		sbr.append(".");
		sbr.append(name);
		sbr.append("\n----------SUMMARY---------\n");
		sbr.append(summary);
		sbr.append("\n----------METHOD LIST---------\n");
		for(APIMethod mtd: methodList)
			sbr.append(mtd.toString());
		
		
		return sbr.toString();
	}
	public APIClass(String pkg, String name) {
		this.name = name;
		this.pkg = pkg;
		this.fieldList = new ArrayList<APIField>();
		this.constructorList = new ArrayList<APIConstructor>();
		this.methodList = new ArrayList<APIMethod>();
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public APIClass getParentClass() {
		return parentClass;
	}

	public void setParentClass(APIClass parent) {
		this.parentClass = parent;
	}

	public List<APIField> getFieldList() {
		return fieldList;
	}

	public void addFields(APIField field) {
		if(! this.fieldList.contains(field))
			this.fieldList.add(field);
	}

	public List<APIConstructor> getConstructors() {
		return constructorList;
	}

	public void addConstructors(APIConstructor constructor) {
		if(! this.constructorList.contains(constructor))
			this.constructorList.add(constructor);
	}

	public List<APIMethod> getMethod() {
		return methodList;
	}

	public void addMethod(APIMethod method) {
		if(! this.methodList.contains(method))
			this.methodList.add(method);
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getPackage() {
		return pkg;
	}

	public void setPackage(String pkg) {
		this.pkg = pkg;
	}
}
