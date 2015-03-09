package edu.ncsu.csc.ase.apisim.result;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
 
/**
 * 
 * @author Raoul Jetley
 *
 */
public class MappingParser {
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String buffer = preprocess(reader);
				
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		SAXHandler handler = new SAXHandler();

		InputStream stream = new ByteArrayInputStream(buffer.getBytes(StandardCharsets.UTF_8));
		parser.parse(stream, handler);
		// parser.parse(args[0], handler);
		
		FileOutputStream fos = new FileOutputStream(args[1]);
        handler.workbook.write(fos);
        fos.close();
	}

	private static String preprocess(BufferedReader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while(line != null) {
			if(line.contains("<![CDATA[")) {
				line = line.replace("<![CDATA[", "");
				line = line.replace("]]>", "");
				line = line.replaceAll("&&", "--AND--");
				line = line.replaceAll("<", "--LBRACKET--");
				line = line.replaceAll(">", "--RBRACKET--");
			}
			buffer.append(line);
			// buffer.append("\n");
			line = reader.readLine();
		}
		return buffer.toString();
	}
}

/**
 * The Handler for SAX Events.
26
 */

class SAXHandler extends DefaultHandler2 {
	String srcPkgName = null;
	String targetPkgName = null;
	String srcClassName = null;
	String targetClassName = null;
	String srcMethodSignature = null;
	String srcMethodName = null;
	String targetMethod = null;
	String targetProperty = null;
	
	String content = "";
	
	Workbook workbook = null;
	Sheet sheet = null;
	Row row = null;
	
	int rowIndex = 0;
	int colIndex = 0;
	
	boolean inMethod = false; // Flag to deal with discrepancy of target name
	
	public SAXHandler() {
		super();
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Mapping");
		row = sheet.createRow(rowIndex++);
		Cell cell0 = row.createCell(0);
		cell0.setCellValue("Source Class");
		Cell cell1 = row.createCell(1);
		cell1.setCellValue("Target Class");
		Cell cell2 = row.createCell(2);
		cell2.setCellValue("Source and Target Methods/Properties");
	}

	@Override
	//Triggered when the start of tag is found.
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		switch(qName) {
		// Create a new Employee object when the start tag is found
			case "package":
				srcPkgName = attributes.getValue("name");
				break;
			case "class":
				System.out.println(); ////
				row = sheet.createRow(rowIndex++);
				colIndex = 0;
				
				srcClassName = attributes.getValue("name");

				System.out.print(srcPkgName+"."+srcClassName+", ");	////			
				Cell srcClassCell = row.createCell(colIndex++);
				srcClassCell.setCellValue(srcPkgName+"."+srcClassName);
				
				break;
			case "target":
				targetPkgName = attributes.getValue("packageName");
				if(targetPkgName!=null && !targetPkgName.equals("") && !inMethod) {
					targetClassName = targetPkgName + "." + attributes.getValue("name");
					System.out.print(targetClassName); ////
					Cell tgtClassCell = row.createCell(colIndex++);
					tgtClassCell.setCellValue(targetClassName);
				}
				if(inMethod) {
					targetProperty = attributes.getValue("propertyGet");
					if(targetProperty!=null && !targetProperty.equals("")) {
						System.out.print(", " + targetProperty); ////
						Cell tgtPropertyCell = row.createCell(colIndex++);
						tgtPropertyCell.setCellValue(targetProperty);
					}
					targetProperty = attributes.getValue("propertySet");
					if(targetProperty!=null && !targetProperty.equals("")) {
						System.out.print(", " + targetProperty); ////
						Cell tgtPropertyCell = row.createCell(colIndex++);
						tgtPropertyCell.setCellValue(targetProperty);
					}
					targetProperty = attributes.getValue("name");
					if(targetProperty!=null && !targetProperty.equals("")) {
						System.out.print(", " + targetProperty); ////
						Cell tgtPropertyCell = row.createCell(colIndex++);
						tgtPropertyCell.setCellValue(targetProperty);
					}
				}
				break;
			case "method":
				inMethod = true;
				srcMethodSignature = attributes.getValue("signature");
				srcMethodName = attributes.getValue("name");
				if (srcMethodSignature!=null) {
					System.out.print(", " + srcMethodName + srcMethodSignature); ////
					Cell srcMethodCell = row.createCell(colIndex++);
					srcMethodCell.setCellValue(srcMethodName + srcMethodSignature);
				}
				break;
			case "constructor":
				srcMethodSignature = attributes.getValue("signature");
				srcMethodName = srcClassName;
				if (srcMethodSignature!=null) {
					System.out.print(", " + srcMethodName + srcMethodSignature); ////
					Cell srcMethodCell = row.createCell(colIndex++);
					srcMethodCell.setCellValue(srcMethodName + srcMethodSignature);
				}
				break;
			case "format":
				break;
		}
	}

 

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch(qName) {
			case "package":
				break;
			case "class":
				break;
			case "target":
				break;
			case "method":
				inMethod= false;
				break;
			case "format":
				content = content.replaceAll("--AND--", "&&");
				content = content.replaceAll("--LBRACKET--", "<");
				content = content.replaceAll("--RBRACKET--", ">");
				System.out.print(", " + content); ////
				Cell tgtMethodCell = row.createCell(colIndex++);
				tgtMethodCell.setCellValue(content);
				content = "";
				break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = content.concat(String.copyValueOf(ch, start, length).trim());
		// System.out.println("Content = " + content);
	}
	
}