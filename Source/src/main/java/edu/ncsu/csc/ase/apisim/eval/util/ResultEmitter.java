package edu.ncsu.csc.ase.apisim.eval.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.eval.oracle.MyOracle;

public class ResultEmitter 
{
	@Deprecated
	public static void writeDataToExcelOld(Map<String, Map<APIMtd, List<Document>>> resultList, String fileName) throws Exception
	{
		
		File f = new File(fileName);
		if(f.exists())
		{
			System.out.println("Old Results File Found");
			FileUtils.deleteQuietly(f);
			System.out.println("Old Results File Deleted");
			
		}
		
		HSSFWorkbook myWorkBook = new HSSFWorkbook();
		HSSFSheet mySheet;
		
		HSSFRow myRow = null;
		int i=0;
		for(String clazz: resultList.keySet())
		{
			i=0;
			Map<APIMtd, List<Document>> result = resultList.get(clazz);
			mySheet = myWorkBook.createSheet(clazz);
			for(APIMtd mtd : result.keySet())
			{
				List<Document> mappingList = result.get(mtd);
				myRow = mySheet.createRow(i);
				myRow.createCell(0).setCellValue(mtd.getName());
				myRow.createCell(1).setCellValue(mtd.getDescription());
				i++;
				for(Document doc: mappingList)
				{
					myRow = mySheet.createRow(i);
					myRow.createCell(0).setCellValue("");
					myRow.createCell(1).setCellValue(doc.get(Configuration.IDX_FIELD_CLASS_NAME));
					myRow.createCell(2).setCellValue(doc.get(Configuration.IDX_FIELD_METHOD_NAME));
					myRow.createCell(3).setCellValue(doc.get(Configuration.IDX_FIELD_DESCRIPTION));
					i++;
				}
			}
		}
		FileOutputStream out = new FileOutputStream(fileName);
		myWorkBook.write(out);
		out.close();
	}
	
	public static void writeDataToExcel(Map<String, Map<APIMtd, List<Document>>> resultList, String fileName) throws Exception
	{
		
		File f = new File(fileName);
		if(f.exists())
		{
			System.out.println("Old Results File Found");
			FileUtils.deleteQuietly(f);
			System.out.println("Old Results File Deleted");
			
		}
		
		
		SXSSFWorkbook myWorkBook = new SXSSFWorkbook();
		Sheet mySheet;
		
		Row myRow = null;
		int i=1;
		String relavant, exact, srcClass, srcMtd, srcMtdDesc, tgtClass, tgtMtd, tgtMtdDesc;
		for(String clazz: resultList.keySet())
		{
			
			i=1;
			Map<APIMtd, List<Document>> result = resultList.get(clazz);
			mySheet = myWorkBook.createSheet(clazz);
			createHeader(mySheet.createRow(0));
			for(APIMtd mtd : result.keySet())
			{
				for(Document doc: result.get(mtd))
				{
					
					srcClass = mtd.getParentClass().getPackage().trim() +"." + mtd.getParentClass().getName().trim();
					srcMtd = mtd.getName();
					srcMtdDesc = mtd.getDescription();
					tgtClass = doc.get(Configuration.IDX_FIELD_CLASS_NAME);
					tgtMtd = doc.get(Configuration.IDX_FIELD_METHOD_NAME);
					tgtMtdDesc = doc.get(Configuration.IDX_FIELD_DESCRIPTION);
					relavant = MyOracle.getInstance().isRelavant(srcClass, srcMtd, tgtClass, tgtMtd);
					exact = MyOracle.getInstance().isExactMatch(srcClass, srcMtd, tgtClass, tgtMtd);
					
					myRow = mySheet.createRow(i);
					
					myRow.createCell(0).setCellValue(relavant);
					myRow.createCell(1).setCellValue(exact);
					myRow.createCell(2).setCellValue(srcClass);
					myRow.createCell(3).setCellValue(srcMtd);
					myRow.createCell(4).setCellValue(srcMtdDesc);
					myRow.createCell(5).setCellValue(tgtClass);
					myRow.createCell(6).setCellValue(tgtMtd);
					myRow.createCell(7).setCellValue(tgtMtdDesc);
					i++;
					
					
				}
				mySheet.createRow(i);
				i++;
			}
		}
		FileOutputStream out = new FileOutputStream(fileName);
		myWorkBook.write(out);
		out.close();
	}
	
	private static void createHeader(Row myRow) 
	{
		myRow.createCell(0).setCellValue("Relevant");
		myRow.createCell(1).setCellValue("Exact");
		myRow.createCell(2).setCellValue("Source Class");
		myRow.createCell(3).setCellValue("Source Method");
		myRow.createCell(4).setCellValue("Source Method Desc");
		myRow.createCell(5).setCellValue("Target Class");
		myRow.createCell(6).setCellValue("Target Method");
		myRow.createCell(7).setCellValue("Target Method Desc");
		
		
	}

	private static void createResultExcel(String fileName)
	{
		File f = new File(fileName);
		if(f.exists())
		{
			System.out.println("Old Results File Found");
			FileUtils.deleteQuietly(f);
			System.out.println("Old Results File Deleted");
			
		}
		
		SXSSFWorkbook myWorkBook = new SXSSFWorkbook();
		
		myWorkBook.createSheet("Alert");
		myWorkBook.createSheet("Command");
		myWorkBook.createSheet("Font");
		myWorkBook.createSheet("Displayable");
		myWorkBook.createSheet("Display");
		myWorkBook.createSheet("Canvas");
		myWorkBook.createSheet("GameCanvas");
		myWorkBook.createSheet("Layer");
		myWorkBook.createSheet("Sprite");
		myWorkBook.createSheet("Graphics");
		
		try 
		{
		    FileOutputStream out = new FileOutputStream(new File(fileName));
		    myWorkBook.write(out);
		    out.close();
		    System.out.println("Excel written successfully..");
		     
		} 
		catch (FileNotFoundException e) 
		{
		    e.printStackTrace();
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		createResultExcel("abc.xlsx");
	}
	
}
