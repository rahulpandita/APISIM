package edu.ncsu.csc.ase.apisim.dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class APIMtd implements Serializable
{
	
	private String modifier;
	
	private String name;
	
	private APIType parentClass;
	
	private List<String> parameterList;
	
	private List<String> exceptionList;
	
	private String returnType;
	
	private String description;
	
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
		sbr.append(name);
		sbr.append("\n");
		sbr.append(description);
		sbr.append("\n-----------------------\n");
		return sbr.toString();
	}
	public APIMtd(String modifier, String name) {
		this.modifier = modifier;
		this.name = name;
		this.parameterList = new ArrayList<>();
		this.exceptionList = new ArrayList<>();
	}
	
	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public APIType getParentClass() {
		return parentClass;
	}

	public void setParentClass(APIType parentClass) {
		this.parentClass = parentClass;
	}

	public List<String> getParameterList() {
		return parameterList;
	}

	public void addParameter(String parameter) {
		if(!this.parameterList.contains(parameter))
			parameterList.add(parameter);
	}

	public List<String> getExceptionList() {
		return exceptionList;
	}

	public void addException(String exception) {
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
