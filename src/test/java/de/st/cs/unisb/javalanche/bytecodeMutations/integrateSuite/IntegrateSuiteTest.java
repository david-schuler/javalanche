package org.softevo.mutation.bytecodeMutations.integrateSuite;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Test;
import org.softevo.mutation.bytecodeMutations.integrateSuite.testClasses.AllTests;
import org.softevo.mutation.runtime.testsuites.SelectiveTestSuite;
import org.softevo.mutation.runtime.testsuites.TestSuiteUtil;

public class IntegrateSuiteTest {

	@Test
	public void runTests() {
		TestSuite suite = AllTests.suite();
		Map<String, junit.framework.Test> map = TestSuiteUtil.getAllTests(suite);
		suite.run(new TestResult());
		assertTrue(suite instanceof SelectiveTestSuite);
		assertTrue(map.size() > suite.testCount());
	}

}
