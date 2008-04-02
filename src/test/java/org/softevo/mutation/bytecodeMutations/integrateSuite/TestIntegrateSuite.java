package org.softevo.mutation.bytecodeMutations.integrateSuite;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Test;
import org.softevo.mutation.bytecodeMutations.integrateSuite.testClasses.AllTests;
import org.softevo.mutation.runtime.testsuites.SelectiveTestSuite;

public class TestIntegrateSuite {

	@Test
	public void runTests() {
		TestSuite suite = AllTests.suite();
		Map<String, TestCase> map = SelectiveTestSuite.getAllTests(suite);
		suite.run(new TestResult());
		assertTrue(suite instanceof SelectiveTestSuite);
		assertTrue(map.size() > suite.testCount());
	}

}
