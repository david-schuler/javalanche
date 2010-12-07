package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class Junit4PropertyTestSuiteTest {

	private static final String TEST_CLASS_NAME = "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunit4Test";
	private static final String TEST_CLASS_NAME_JUNIT3 = "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunitTest";

	@Test
	public void testClassProperty() {

		MutationProperties.TEST_CLASSES = TEST_CLASS_NAME;
		Junit4PropertyTestSuite placeholderTestSuite = new Junit4PropertyTestSuite(
				Junit4PropertyTestSuite.class);
		int testCount = placeholderTestSuite.testCount();
		assertEquals("Expected different number of testcases", 3, testCount);
		MutationProperties.TEST_CLASSES = null;
	}

	@Test
	public void testMethodProperty() {
		MutationProperties.TEST_METHODS = "true";
		MutationProperties.TEST_SUITE = TEST_CLASS_NAME + ".test1:"
				+ TEST_CLASS_NAME + ".test2";
		Junit4PropertyTestSuite suite = new Junit4PropertyTestSuite(
				Junit4PropertyTestSuite.class);
		int testCaseCount = suite.testCount();
		assertEquals("Expected different number of testcases", 2, testCaseCount);
		MutationProperties.TEST_METHODS = null;
		MutationProperties.TEST_SUITE = null;
	}

	@Test
	public void testMethodPropertyJunit3() {
		MutationProperties.TEST_METHODS = "true";
		MutationProperties.TEST_SUITE = TEST_CLASS_NAME_JUNIT3 + ".test1:"
				+ TEST_CLASS_NAME_JUNIT3 + ".test2";
		Junit4PropertyTestSuite suite = new Junit4PropertyTestSuite(
				Junit4PropertyTestSuite.class);
		int testCaseCount = suite.testCount();
		assertEquals("Expected different number of testcases", 2, testCaseCount);
		MutationProperties.TEST_METHODS = null;
		MutationProperties.TEST_SUITE = null;
	}

	@Test
	public void testNoProp() {
		MutationProperties.TEST_METHODS = null;
		MutationProperties.TEST_CLASSES = null;
		try {
			new Junit4PropertyTestSuite(Junit4PropertyTestSuite.class);
			fail("Expected exception");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	@Test
	public void testNoExceptionClassNotFound() {
		MutationProperties.TEST_CLASSES = "NonExisitingClass.m1";
		MutationProperties.TEST_METHODS = null;
		new Junit4PropertyTestSuite(Junit4PropertyTestSuite.class);
		MutationProperties.TEST_CLASSES = null;
		MutationProperties.TEST_METHODS = "NonExisitingClass.m1";
		new Junit4PropertyTestSuite(Junit4PropertyTestSuite.class);
		MutationProperties.TEST_METHODS = null;
	}
}
