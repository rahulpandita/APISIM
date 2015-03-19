package edu.ncsu.csc.realsearch.apisim.eval;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.Query;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.realsearch.apisim.cache.Cache;
import edu.ncsu.csc.realsearch.apisim.databeans.SearchResultBean;
import edu.ncsu.csc.realsearch.apisim.util.LuceneQueryHelper;



public class JME_Eval {
	public static void main(String[] args) {
		Cache cc = Cache.getInstance();
		Map<String, APIType> classMap = cc.getClassIdx();
		Map<APIMtd, List<SearchResultBean>> res;
		Map<String, Map<APIMtd, List<SearchResultBean>>> resultList = new LinkedHashMap<String, Map<APIMtd,List<SearchResultBean>>>();
		
		for(String clazz: classMap.keySet())
		{
			res = new LinkedHashMap<APIMtd, List<SearchResultBean>>();
			Map<String, String> mtdMap = cc.getClassMtdIdx(clazz);
			for(int i=0; i< mtdMap.keySet().size();i++)
			{
				
				APIMtd mtd = cc.getClassMtdAtIdx(clazz, i);
				Query q = LuceneQueryHelper.getQuery(mtd);
				try {
					List<SearchResultBean> resList = LuceneQueryHelper.executeQuery(q);
					res.put(mtd, resList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			resultList.put(clazz, res);
		}
		
		try {
			writeDataToExcel(resultList, "jav sql.xlsx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeDataToExcel(Map<String, Map<APIMtd, List<SearchResultBean>>> resultList, String fileName) throws Exception
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
		String relavant, exact, srcClass, srcMtd, srcMtdDesc, tgtClass, tgtMtd, tgtMtdDesc, score;
		for(String clazz: resultList.keySet())
		{
			
			i=1;
			Map<APIMtd, List<SearchResultBean>> result = resultList.get(clazz);
			mySheet = myWorkBook.createSheet(clazz);
			createHeader(mySheet.createRow(0));
			for(APIMtd mtd : result.keySet())
			{
				for(SearchResultBean doc: result.get(mtd))
				{
					
					srcClass = mtd.getParentClass().getPackage().trim() +"." + mtd.getParentClass().getName().trim();
					srcMtd = mtd.getName();
					srcMtdDesc = mtd.getDescription();
					tgtClass = doc.getPkgName()+"."+ doc.getClsName();
					tgtMtd = doc.getMtdName();
					tgtMtdDesc = doc.getMtdDesc();
					score = doc.getScore();
					relavant = "";//MyOracle.getInstance().isRelavant(srcClass, srcMtd, tgtClass, tgtMtd);
					exact = "";//MyOracle.getInstance().isExactMatch(srcClass, srcMtd, tgtClass, tgtMtd);
					
					myRow = mySheet.createRow(i);
					
					myRow.createCell(0).setCellValue(relavant);
					myRow.createCell(1).setCellValue(exact);
					myRow.createCell(2).setCellValue(srcClass);
					myRow.createCell(3).setCellValue(srcMtd);
					myRow.createCell(4).setCellValue(srcMtdDesc);
					myRow.createCell(5).setCellValue(tgtClass);
					myRow.createCell(6).setCellValue(tgtMtd);
					myRow.createCell(7).setCellValue(tgtMtdDesc);
					myRow.createCell(8).setCellValue(score);
					
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

}
