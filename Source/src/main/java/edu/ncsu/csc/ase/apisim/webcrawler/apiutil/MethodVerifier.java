package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

class MethodVerifier extends ASTVisitor
{
	private boolean retValue;
	
	private String methodName;
	
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
		return false;
	}
}