package edu.ncsu.csc.ase.apisim.dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class APIType implements Serializable 
{
	private String name;
	
	private String modifier;
	
	private String apiName;
	
	private List<APIField> fieldList;
	
	private List<APIMtd> constructorList;
	
	private List<APIMtd> methodList;
	
	private String summary;
	
	private String pkg;
	
	private String url;
	
	private List<String> implementsList;
		
	private List<String> extendsList;
	
	private boolean interfaze;
	
	private boolean enums;
	
	private boolean typeAnnotated = false;
	
	public boolean isTypeAnnotated() {
		return typeAnnotated;
	}
	public void setTypeAnnotated(boolean typeAnnotated) {
		this.typeAnnotated = typeAnnotated;
	}
	@Override
	public String toString() {
		StringBuffer sbr = new StringBuffer();
		sbr.append(pkg);
		sbr.append(".");
		sbr.append(name);
		sbr.append("\n----------SUMMARY---------\n");
		sbr.append(summary);
		sbr.append("\n----------METHOD LIST---------\n");
		for(APIMtd mtd: constructorList)
			sbr.append(mtd.toString());
		for(APIMtd mtd: methodList)
			sbr.append(mtd.toString());
		
		
		return sbr.toString();
	}
	
	public APIType(String pkg, String name) {
		this.name = name;
		this.pkg = pkg;
		this.fieldList = new ArrayList<APIField>();
		this.constructorList = new ArrayList<APIMtd>();
		this.methodList = new ArrayList<APIMtd>();
		this.implementsList = new ArrayList<String>();
		this.extendsList = new ArrayList<String>();
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

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public List<APIField> getFieldList() {
		return fieldList;
	}

	public void addFields(APIField field) {
		if(! this.fieldList.contains(field))
			this.fieldList.add(field);
	}

	public List<APIMtd> getConstructors() {
		return constructorList;
	}

	public void addConstructors(APIMtd constructor) {
		if(! this.constructorList.contains(constructor))
			this.constructorList.add(constructor);
	}

	public List<APIMtd> getMethod() {
		return methodList;
	}

	public void addMethod(APIMtd method) {
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getImplementsList() {
		return implementsList;
	}
	public void addImplementsList(String implementType) 
	{
		if(this.implementsList == null)
		{
			this.implementsList = new ArrayList<>();
		}
		if(!this.implementsList.contains(implementType))
		{
			this.implementsList.add(implementType);
		}
	}
	
	public List<String> getExtendsList() 
	{
		return extendsList;
	}
	
	public void addExtendsList(String extendsType) 
	{
		if(this.extendsList == null)
		{
			this.extendsList = new ArrayList<>();
		}
		if(!this.extendsList.contains(extendsType))
		{
			this.extendsList.add(extendsType);
		}
	}
	public boolean isInterfaze() {
		return interfaze;
	}
	public void setInterfaze(boolean interfaze) {
		this.interfaze = interfaze;
	}
	public boolean isEnums() {
		return enums;
	}
	public void setEnums(boolean enums) {
		this.enums = enums;
	}
}
