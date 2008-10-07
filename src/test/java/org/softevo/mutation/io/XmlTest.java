package org.softevo.mutation.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.softevo.mutation.coverageResults.CoverageResult;

public class XmlTest {

	private static final File FILE = new File("test-xml-testfile.xml");

	@Test
	public void testReadWrite() {
		CoverageResult cr = new CoverageResult("testclass");
		List<Integer> lines = new ArrayList<Integer>();
		cr.addTestCase("testcase1", lines);
		cr.addTestCase("testcase2", lines);
		cr.addTestCase("testcase3", lines);
		XmlIo.toXML(cr, FILE);
		FILE.deleteOnExit();
		CoverageResult cr2 = (CoverageResult) XmlIo.fromXml(FILE);
		Assert.assertNotNull(cr2);
	}
}
