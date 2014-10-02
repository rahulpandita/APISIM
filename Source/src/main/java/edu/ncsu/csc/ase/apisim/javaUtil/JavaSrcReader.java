package edu.ncsu.csc.ase.apisim.javaUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.webcrawler.AllClassCrawler;

/**
 * Class responsible for reading the java src.zip
 * @author Rahul Pandita
 * @date 10/01/2014
 */
public class JavaSrcReader {
	
	private static Map<String, APIType> typeMap = new HashMap<String, APIType>();
	
	
	public static void main(String[] args) throws Exception{
		String srcPath = "data\\src.zip";
		String extention = ".java";
		JavaSrcReader jsr = new JavaSrcReader();
		jsr.readSrcZip(srcPath, extention);
	}

	/**
	 * @param srcPath
	 * @throws IOException
	 */
	public void readSrcZip(String srcPath, String extention) throws IOException {
		try(ZipFile zipFile = new ZipFile(srcPath);)
		{
		    Enumeration<? extends ZipEntry> entries = zipFile.entries();
		    InputStream stream;
		    StringBuffer buff;
		    List<APIType> typList;
		    CompilationUnit cu;
		    while(entries.hasMoreElements())
		    {
		        ZipEntry entry = entries.nextElement();
		        if(entry.getName().endsWith(extention))
		        {
		        	//System.out.println(entry.getName());
		        	stream = zipFile.getInputStream(entry);
		        	try(Scanner scanner = new Scanner(new InputStreamReader(stream));)
		        	{
			        	buff= new StringBuffer();
			        	while (scanner.hasNextLine()) {
			        		buff.append(scanner.nextLine());
			        		buff.append("\n");
			        	}	
			        	cu  = createCompilationUnit(buff.toString().toCharArray());
			        	typList = getAPIType(cu);
			        	for(APIType typ:typList)
			        	{
			        		typeMap.put(typ.getPackage()+"."+typ.getName(), typ);
			        	}
			        	
		        	}	
		        }
		    }
		    
		   
		    List<APIType> typLst = new ArrayList<APIType>();
		    typLst.addAll(typeMap.values());
		    AllClassCrawler.write(typLst, Configuration.JAVA_DUMP_PATH);
		    
		}
	    
	}
	
	private List<APIType> getAPIType(CompilationUnit cu) {
		JavaAPITypeVisitor visitor = new JavaAPITypeVisitor();
		cu.accept(visitor);
		//TODO Decorate with TypeInformation
		return visitor.getApiTypeList();
	}

	private CompilationUnit createCompilationUnit(char[] srcStr)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setSource(srcStr);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = null;
		try
		{
			cu = (CompilationUnit) parser.createAST(null);
			
		}	
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return cu;	
	}
}
