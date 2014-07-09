package edu.ncsu.csc.ase.apisim.spring.mvc.displayBeans;

public class Result {
	
	private String methodName;
	
	private String className;
	
	private String description;
	
	public Result(String className, String methodName, String description)
	{
		this.methodName = methodName;
		this.className = className;
		this.description = description;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
