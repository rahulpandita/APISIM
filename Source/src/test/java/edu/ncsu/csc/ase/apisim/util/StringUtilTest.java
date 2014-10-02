package edu.ncsu.csc.ase.apisim.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {
	
	@Test
	public void test1() {
		String testStr = "Create a new SAXParseException with an embedded exception.\n<p>This constructor is most useful for parser writers who\nneed to wrap an exception that is not a subclass of\n{@link org.xml.sax.SAXException SAXException}\n.</p>\n<p>All parameters except the message and exception are as if\nthey were provided by a \n{@link Locator}\n.  For example, if the\nsystem identifier is a URL (including relative filename), the\ncaller must resolve it fully before creating the exception.</p>";
		String actual = StringUtil.cleanHTML(testStr);
		String expected = "Create a new SAXParseException with an embedded exception. <p>This constructor is most useful for parser writers who need to wrap an exception that is not a subclass of {@link org.xml.sax.SAXException SAXException} .</p> <p>All parameters except the message and exception are as if they were provided by a {@link Locator} . For example, if the system identifier is a URL (including relative filename), the caller must resolve it fully before creating the exception.</p>";
		Assert.assertTrue(actual.equals(expected));
	}
	
}
