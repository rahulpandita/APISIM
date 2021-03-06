package edu.ncsu.csc.ase.apisim.eval.oracle;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;


/**
 * This Class is responsible to compute the benefit over 
 * <a href="http://paul.rutgers.edu/~amrutag/papers/icse2013/icse2013-preprint.pdf">Rossetta results</a>
 * 
 * @author Rahul Pandita
 *
 */
public class RosettaOracle 
{
	
	
	public static Map<String, List<String>> read() throws Exception {
		
		Map<String, List<String>> retMap = new HashMap<String, List<String>>(); 
		FileInputStream fileInputStream = new FileInputStream(Configuration.ROSETTA_RESULTS);
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		HSSFSheet worksheet = workbook.getSheetAt(0);
		HSSFRow row;
		String sourceMtd, targetMtd;
		for(int i=0; i< worksheet.getPhysicalNumberOfRows();i++)
		{
			row = worksheet.getRow(i);
			sourceMtd = getStringValue(row.getCell(0)) + "_" + getStringValue(row.getCell(1));
			targetMtd = getStringValue(row.getCell(2)) + "_" + getStringValue(row.getCell(3));
			List<String> tgtMtdList = new ArrayList<String>();
			tgtMtdList.add(targetMtd);
			if(getStringValue(row.getCell(4))!="")
			{
				//targetMtd = targetMtd + ";" + getStringValue(row.getCell(4)) + "_" + getStringValue(row.getCell(5));
				tgtMtdList.add(getStringValue(row.getCell(4)) + "_" + getStringValue(row.getCell(5)));
			}
			
			if(retMap.containsKey(sourceMtd))
			{
				retMap.get(sourceMtd).addAll(tgtMtdList);
				//retMap.get(sourceMtd).add(targetMtd);
			}
			else
			{
				List<String> targetMtdList = new ArrayList<String>();
				targetMtdList.addAll(tgtMtdList);
				//targetMtdList.add(targetMtd);
				retMap.put(sourceMtd, targetMtdList);
			}	
		}
		fileInputStream.close();
		System.out.println("Read "+worksheet.getPhysicalNumberOfRows() + " rows.");
		return retMap;
	}

	/**
	 * Sanitize nulls
	 * @param row
	 * @return
	 */
	private static String getStringValue(HSSFCell cell) 
	{
		return cell==null?"":cell.getStringCellValue().trim();
	}
	
	public static void main(String[] args) throws Exception {
		read();
	}
	
}
