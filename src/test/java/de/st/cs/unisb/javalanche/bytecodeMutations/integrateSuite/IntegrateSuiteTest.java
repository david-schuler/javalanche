package de.st.cs.unisb.javalanche.bytecodeMutations.integrateSuite;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Test;
import de.st.cs.unisb.javalanche.bytecodeMutations.integrateSuite.testClasses.AllTests;
import de.st.cs.unisb.javalanche.runtime.testsuites.SelectiveTestSuite;
import de.st.cs.unisb.javalanche.runtime.testsuites.TestSuiteUtil;

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
