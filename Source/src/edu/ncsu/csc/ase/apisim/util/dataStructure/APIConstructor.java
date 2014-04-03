package edu.ncsu.csc.ase.apisim.util.dataStructure;

public class APIConstructor extends APIMethod{

	private boolean constructor;
	
	public boolean isConstructor() {
		return constructor;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	public APIConstructor(String modifier, String name) {
		super(modifier, name);
		this.constructor = true;
	}
	

}
