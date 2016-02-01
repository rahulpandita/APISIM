package edu.ncsu.csc.ase.apisim.topicModel;

import cc.mallet.types.Instance;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

public class DataInstance extends Instance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataInstance(String data, String source) {
		super(StringUtil.splitCamelCase(data), 3, 2, source);
		
	}
	
	public DataInstance(APIType type) 
	{
		super(StringUtil.splitCamelCase(type.getSummary()), 3, 2, type.getApiName()+":"+type.getPackage()+"."+type.getName());
	}

	public DataInstance(boolean b, APIType type) {
		super(type.toString()==null?"":StringUtil.splitCamelCase(type.toString()), 
				3, 
				2, 
				type.getApiName()==null?"":type.getApiName()+":"+
				type.getPackage()==null?"":type.getPackage()+"."+
				type.getName()==null?"":type.getName());
	}
}
