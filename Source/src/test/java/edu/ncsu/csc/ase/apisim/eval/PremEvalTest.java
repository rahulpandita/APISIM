package edu.ncsu.csc.ase.apisim.eval;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Assert;
import org.junit.Test;

import edu.ncsu.csc.ase.apisim.util.FileUtil;

public class PremEvalTest 
{
	@Test
	public void testSimialrity() {
		
		Collection<File> files = FileUtils.listFiles(new File("premResultsOld"), new SuffixFileFilter("txt"),DirectoryFileFilter.DIRECTORY); 
		File file2 = null;
		for(File file:files)
		{
			System.out.println(FileUtil.readString(file));
			file2 = new File(file.getAbsolutePath().replace("premResultsOld", "premResults"));
			Assert.assertTrue(FileUtil.readString(file).equalsIgnoreCase(FileUtil.readString(file2)));
		}
	}
}

