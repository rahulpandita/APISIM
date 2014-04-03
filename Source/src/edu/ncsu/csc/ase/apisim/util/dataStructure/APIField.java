package edu.ncsu.csc.ase.apisim.util.dataStructure;

import java.util.List;

public class APIField 
{
	private String modifier;
	
	private String name;
	
	private APIClass parentClass;
	
	private String description;

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
