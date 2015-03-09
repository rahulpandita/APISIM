package edu.ncsu.csc.ase.apisim.javaUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jsoup.Jsoup;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIField;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.util.StringUtil;

/**
 * 
 * @author Rahul Pandita
 *
 */
public class JavaAPITypeVisitor extends ASTVisitor{
	
	private List<APIType> apiTypeList;
	
	private Stack<APIType> curType = new Stack<APIType>();
	
	private String pkgStr = "";
	
	private APITYPE api_type;
	
	public JavaAPITypeVisitor(APITYPE type) {
		this.api_type = type;
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
			
			if(node.getName().getFullyQualifiedName().equalsIgnoreCase("LinkedHashMap"))
				System.out.println("here");
			
			APIType type = new APIType(pkgStr, node.getName().getIdentifier());
			type.setApiName(api_type.name());
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
		if(Modifier.isPublic(node.getModifiers())
				||(curType.peek().isInterfaze() && (Modifier.toString(node.getModifiers())).trim().equals("")))
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
				{
					buff.append(((Name)subObj).getFullyQualifiedName());
				}
				else if(subObj instanceof TextElement)
				{
					buff.append(subObj.toString());
				}
				else if((subObj instanceof MemberRef) || (subObj instanceof MethodRef))
				{
					buff.append((subObj.toString().replaceAll(Pattern.quote("#"), " ")).trim());
				}
				else if(subObj instanceof TagElement)
				{
					TagElement subte = (TagElement)subObj;
					switch (subte.getTagName()) {
					case TagElement.TAG_LINK:
						if(subte.fragments().size()<=1)
							buff.append(subte.fragments().get(0));						
						else
						{
							for(Object obj:subte.fragments().subList(1,subte.fragments().size()))
								buff.append(obj.toString()).append(" ");
						}
						break;
					case TagElement.TAG_LINKPLAIN:
						if(subte.fragments().size()<=1)
							buff.append(subte.fragments().get(0));						
						else
						{
							for(Object obj:subte.fragments().subList(1,subte.fragments().size()))
								buff.append(obj.toString()).append(" ");
						}
						break;
					case TagElement.TAG_INHERITDOC:
						break;
					case TagElement.TAG_DOCROOT:
						break;
					case TagElement.TAG_LITERAL:
						if(subte.fragments().size()==1)
							buff.append(subte.fragments().get(0));						
						else if(subte.fragments().size()>2)
							buff.append(subte.fragments().get(0));						
						else
							buff.append(subte.fragments().get(1));
						break;
					case TagElement.TAG_CODE:
						for(Object obj:subte.fragments())
							buff.append(obj.toString()).append(" ");
						break;
					default:
							buff.append(subte.toString());
						break;
					}
				}
				else
				{
					System.out.println(subObj.getClass());
				}
				buff.append(" ");
			}
			
			buff.append("\n");
		}
		return buff.toString().replaceAll("\\s+", " ").trim();
	}
	

}
