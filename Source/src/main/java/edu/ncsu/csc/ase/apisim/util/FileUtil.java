package edu.ncsu.csc.ase.apisim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling File related operations
 * 
 * @author Rahul Pandita
 * 
 */
public class FileUtil {
	
	public static boolean writeStringtoFile(String file, String contents) {
		return writeStringtoFile(file, contents, false);
	}

	public static boolean writeStringtoFile(String file, String contents, boolean append) 
	{
		boolean retValue = true;
		BufferedWriter writer = null;
		try 
		{
			// create a temporary file
			File logFile = new File(file);

			writer = new BufferedWriter(new FileWriter(logFile,append));
			writer.append(contents);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) { }
			retValue = false;
		}
		return retValue;
	}
	
	
	public static String readString(File file) {
		StringBuffer buff = new StringBuffer();
		List<String> lines = readLines(file);
		for(String line: lines)
		{
			buff.append(line);
			buff.append("\n");
		}
		return buff.toString().trim();
	}
	
	public static List<String> readLines(File file) {
		List<String> buff = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			String line = null;
			while ((line = reader.readLine()) != null) {
				buff.add(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return buff;
	}
}
