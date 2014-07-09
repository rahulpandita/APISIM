package edu.ncsu.csc.ase.apisim.dataStructure;

public class APIField 
{
	private String modifier;
	
	private String name;
	
	private APIType parentClass;
	
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

	public APIType getParentClass() {
		return parentClass;
	}

	public void setParentClass(APIType parentClass) {
		this.parentClass = parentClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
