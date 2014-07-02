package edu.ncsu.csc.ase.apisim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
}
