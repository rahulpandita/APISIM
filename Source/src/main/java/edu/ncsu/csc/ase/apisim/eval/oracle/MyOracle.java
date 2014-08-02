package edu.ncsu.csc.ase.apisim.eval.oracle;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;

public class MyOracle {
	private static final String SEPERATOR_CHAR = File.separator;

	private static final String RELAVANT = "Relavant";

	private static final String EXACT = "Exact";

	private static final String IRRELAVANT = "Irrelavant";

	private static Map<String, Map<String, List<String>>> cache;

	private static MyOracle instance;

	public static void main(String[] args) {
		getInstance();
	}

	public static synchronized MyOracle getInstance() {
		if (instance == null) {
			instance = new MyOracle();
		}
		return instance;
	}

	private MyOracle() {
		initializeMap();
	}

	private void initializeMap() {
		cache = new HashMap<>();
		try {
			popluate();
			popluate1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void popluate1() throws Exception {
		FileInputStream fileInputStream = new FileInputStream(Configuration.ORACLE1);
		Workbook workbook = new XSSFWorkbook(fileInputStream);
		Sheet workSheet;
		Row row;
		String srcClassName, srcMtdName, tgtClassName, tgtMtdName;
		int rel, extMatch;
		for (int wrkSheetIdx = 0; wrkSheetIdx < workbook.getNumberOfSheets(); wrkSheetIdx++) {
			workSheet = workbook.getSheetAt(wrkSheetIdx);
			for (int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++) {
				row = workSheet.getRow(i);
				rel = getIntValue(row.getCell(0));
				extMatch = getIntValue(row.getCell(1));
				srcClassName = getStringValue(row.getCell(2));
				srcMtdName = getStringValue(row.getCell(3));
				tgtClassName = getStringValue(row.getCell(5));
				tgtMtdName = getStringValue(row.getCell(6));
				if (tgtMtdName != "") {
					if (rel == 1) {
						populateWhiteList(extMatch, srcClassName, srcMtdName,
								tgtClassName, tgtMtdName);
					} else {
						populateBlkList(srcClassName, srcMtdName, tgtClassName,
								tgtMtdName);
					}

				}

			}

		}

	}

	private void popluate() throws Exception {
		FileInputStream fileInputStream = new FileInputStream(
				Configuration.ORACLE);
		Workbook workbook = new XSSFWorkbook(fileInputStream);
		Sheet workSheet;
		Row row;
		String srcClassName, srcMtdName, tgtClassName, tgtMtdName;
		int rel, extMatch;
		for (int wrkSheetIdx = 0; wrkSheetIdx < workbook.getNumberOfSheets(); wrkSheetIdx++) {
			workSheet = workbook.getSheetAt(wrkSheetIdx);
			for (int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++) {
				row = workSheet.getRow(i);
				rel = getIntValue(row.getCell(0));
				extMatch = getIntValue(row.getCell(1));
				srcClassName = getStringValue(row.getCell(5));
				srcMtdName = getStringValue(row.getCell(7));
				tgtClassName = getStringValue(row.getCell(9));
				tgtMtdName = getStringValue(row.getCell(10));
				if (tgtMtdName != "") {
					if (rel == 1) {
						populateWhiteList(extMatch, srcClassName, srcMtdName,
								tgtClassName, tgtMtdName);
					} else {
						populateBlkList(srcClassName, srcMtdName, tgtClassName,
								tgtMtdName);
					}

				}

			}

		}

	}

	private void populateBlkList(String srcClassName, String srcMtdName,
			String tgtClassName, String tgtMtdName) {
		List<String> methodList = getList(srcClassName, srcMtdName, IRRELAVANT);
		if (!methodList.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
			methodList.add(tgtClassName + SEPERATOR_CHAR + tgtMtdName);

	}

	private void populateWhiteList(int extMatch, String srcClassName,
			String srcMtdName, String tgtClassName, String tgtMtdName) {
		List<String> methodList = getList(srcClassName, srcMtdName, RELAVANT);
		if (!methodList.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
			methodList.add(tgtClassName + SEPERATOR_CHAR + tgtMtdName);
		if (extMatch == 1) {
			methodList = getList(srcClassName, srcMtdName, RELAVANT);
			if (!methodList
					.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
				methodList.add(tgtClassName + SEPERATOR_CHAR + tgtMtdName);
		}
	}

	private List<String> getList(String srcClassName, String srcMtdName,
			String type) {
		if (!cache.containsKey(srcClassName + SEPERATOR_CHAR + srcMtdName)) {
			List<String> relavantStr = new ArrayList<String>();
			List<String> exactMatch = new ArrayList<String>();
			List<String> notRelavant = new ArrayList<String>();
			Map<String, List<String>> oracleMap = new HashMap<String, List<String>>();
			oracleMap.put(RELAVANT, relavantStr);
			oracleMap.put(EXACT, exactMatch);
			oracleMap.put(IRRELAVANT, notRelavant);

			cache.put(srcClassName + SEPERATOR_CHAR + srcMtdName, oracleMap);
		}
		return cache.get(srcClassName + SEPERATOR_CHAR + srcMtdName).get(type);
	}

	/**
	 * Sanitize for null cells and Not-a-Number Strings
	 * 
	 * @param row
	 * @return
	 */
	private static Integer getIntValue(Cell cell) {
		if (cell == null)
			return 0;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			return (new Double(cell.getNumericCellValue())).intValue();

		case Cell.CELL_TYPE_STRING:
			String val;
			int intVal = 0;
			try {
				val = cell.getStringCellValue();
				intVal = Integer.parseInt(val);
			} catch (Exception e) {
				System.out.println(e);
			}
			return intVal;
		default:
			return 0;
		}

	}

	/**
	 * Sanitize for null cells and Not-a-Number Strings
	 * 
	 * @param row
	 * @return
	 */
	private static String getStringValue(Cell cell) {
		return cell == null ? "" : cell.getStringCellValue().trim();
	}

	public String isRelavant(String srcClassName, String srcMtdName,
			String tgtClassName, String tgtMtdName) {
		List<String> methodList = getList(srcClassName, srcMtdName, RELAVANT);
		if (methodList.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
			return "1";
		else {
			methodList = getList(srcClassName, srcMtdName, IRRELAVANT);
			if (methodList.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
				return "0";
		}
		return "";
	}

	public String isExactMatch(String srcClassName, String srcMtdName,
			String tgtClassName, String tgtMtdName) {
		List<String> methodList = getList(srcClassName, srcMtdName, EXACT);
		if (methodList.contains(tgtClassName + SEPERATOR_CHAR + tgtMtdName))
			return "1";
		return "";
	}

}
