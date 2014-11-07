package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;

class MethodVerifier extends ASTVisitor
{
	private boolean retValue;
	
	private String methodName;
	
	private String returnType;
	
	public APIMtd mtd = null;
	
	private List<String> prmList = new ArrayList<String>();
	
	public String getMethodName() {
		return methodName;
	}

	public boolean isRetValue() {
		return retValue;
	}

	public MethodVerifier() {
		retValue = false;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {

		retValue = true;
		
		methodName = node.getName().toString();
		//if (node.getReturnType2().isSimpleType())
		if(node.getReturnType2()!=null)
			returnType = node.getReturnType2().toString();
		if(mtd!=null)
		{
			mtd.setReturnType(returnType);
			if(node.parameters()!=null)
			{
				for(Object obj:node.parameters())
					mtd.addParameter(obj.toString());
			}
			
			mtd.setModifier(Modifier.toString(node.getModifiers()));
		}
		if(node.parameters()!=null)
		{
			for(Object obj:node.parameters())
				prmList.add(obj.toString());
		}
		return false;
	}

	public List<String> getParamTypes() {
		return prmList;
	}

	public String getReturnType() {
		return returnType;
	}
}