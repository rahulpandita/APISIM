package edu.ncsu.csc.ase.apisim.javaUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIField;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;

/**
 * 
 * @author Rahul Pandita
 *
 */
public class JavaAPITypeVisitor extends ASTVisitor{
	
	private List<APIType> apiTypeList;
	
	private Stack<APIType> curType = new Stack<APIType>();
	
	private String pkgStr = "";
	
	public JavaAPITypeVisitor() {
		apiTypeList = new ArrayList<>();
	}
	
	public List<APIType> getApiTypeList() {
		return apiTypeList;
	}

	public boolean visit(PackageDeclaration node) {
		pkgStr = node.getName().getFullyQualifiedName();
		return super.visit(node);
	}
	
	public boolean visit(TypeDeclaration node) 
	{
		boolean retVal = false;
		
		
		//Only looking for public API
		if(Modifier.isPublic(node.getModifiers()))
		{
			APIType type = new APIType(pkgStr, node.getName().getIdentifier());
			type.setApiName(Configuration.APITYPE.JAVA.name());
			apiTypeList.add(type);
			type.setModifier(Modifier.toString(node.getModifiers()));
			curType.push(type);
			type.setInterfaze(node.isInterface());
			type.setSummary(getJavadocAsString(node.getJavadoc()));
			
			retVal = retVal || super.visit(node);
		}
		return retVal;
	}
	
	public void endVisit(TypeDeclaration node) {
		if(Modifier.isPublic(node.getModifiers()))
		{
			curType.pop();
		}
	}
	
	public boolean visit(FieldDeclaration node)
	{
		//Only looking for public API
		
		if(Modifier.isPublic(node.getModifiers()))
		{
			if(node.getJavadoc()!= null)
			{
				APIField field = new APIField();
				List<?> frag = node.fragments();
				
				for(Object obj: frag)
				{
					VariableDeclarationFragment fg = (VariableDeclarationFragment)obj;
					field.setName(node.getType().toString() + " " +fg.getName().getIdentifier());
				}
				field.setParentClass(curType.peek());
				field.setModifier(Modifier.toString(node.getModifiers()));
				field.setDescription(getJavadocAsString(node.getJavadoc()));
				curType.peek().addFields(field);
				return super.visit(node);
			}
		}
		return false;
	}
	
	public boolean visit(MethodDeclaration node) 
	{
		//Only looking for public API
		if(Modifier.isPublic(node.getModifiers()))
		{
			APIMtd mtd = new APIMtd(Modifier.toString(node.getModifiers()), node.getName().getIdentifier());
			mtd.setParentClass(curType.peek());
			
			mtd.setDescription(getJavadocAsString(node.getJavadoc()));
			
			if(node.getReturnType2()!=null)
				mtd.setReturnType(node.getReturnType2().toString());
			
			if(node.parameters()!=null)
			{
				List<?> prmLst = node.parameters();
				SingleVariableDeclaration prm;
				for(Object obj: prmLst)
				{
					prm = (SingleVariableDeclaration) obj;
					mtd.addParameter(prm.getType().toString());
				}
			}
			
			if(node.thrownExceptions()!=null)
			{
				List<?> exLst = node.thrownExceptions();
				Name ex;
				for(Object obj: exLst)
				{
					ex = (Name) obj;
					mtd.addException(ex.toString());
				}
			}
			
			if(node.isConstructor())
			{
				mtd.setReturnType(curType.peek().getName());
				curType.peek().addConstructors(mtd);
			}
			else
				curType.peek().addMethod(mtd);
			
			return super.visit(node);
		}
		return false;
	}
	

	private String getJavadocAsString(Javadoc javadoc) {
		if(javadoc==null)
			return "";
		List<?> slt = javadoc.tags();
		StringBuffer buff = new StringBuffer();
		for(Object jDocObj:slt)
		{
			TagElement te = (TagElement)jDocObj;
			if(te.getTagName()!=null)
			{
				buff.append(te.getTagName());
				buff.append(" ");
			}
				
			for(Object subObj: te.fragments())
			{
				if(subObj instanceof Name)
					buff.append(((Name)subObj).getFullyQualifiedName());
				else
					buff.append(subObj.toString());
				buff.append("\n");
			}
			
			buff.append("\n");
		}
		return buff.toString();
	}
	

}
