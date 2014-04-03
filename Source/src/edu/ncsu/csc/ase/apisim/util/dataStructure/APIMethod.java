package edu.ncsu.csc.ase.apisim.util.dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class APIMethod implements Serializable
{
	
	private String modifier;
	
	private String name;
	
	private APIClass parentClass;
	
	private List<APIClass> parameterList;
	
	private List<APIClass> exceptionList;
	
	private APIClass returnType;
	
	private String description;
	
	@Override
	public String toString() {
		StringBuffer sbr = new StringBuffer();
		sbr.append(name);
		sbr.append("\n");
		sbr.append(description);
		sbr.append("\n-----------------------\n");
		return sbr.toString();
	}
	public APIMethod(String modifier, String name) {
		this.modifier = modifier;
		this.name = name;
		this.parameterList = new ArrayList<APIClass>();
		this.exceptionList = new ArrayList<APIClass>();
	}
	
	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
	public APIClass getReturnType() {
		return returnType;
	}

	public void setReturnType(APIClass returnType) {
		this.returnType = returnType;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public APIClass getParentClass() {
		return parentClass;
	}

	public void setParentClass(APIClass parentClass) {
		this.parentClass = parentClass;
	}

	public List<APIClass> getParameterList() {
		return parameterList;
	}

	public void setParameterList(APIClass parameter) {
		if(!this.parameterList.contains(parameter))
			parameterList.add(parameter);
	}

	public List<APIClass> getExceptionList() {
		return exceptionList;
	}

	public void setExceptionList(APIClass exception) {
		if(!this.exceptionList.contains(exception))
			exceptionList.add(exception);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
