//package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
//
//public class Junit3PropertyTestSuiteTest {
//
//	private static final String TEST_CLASS_NAME = "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunitTest";
//
//	@Test
//	public void testClassProperty() {
//		MutationProperties.TEST_CLASSES = TEST_CLASS_NAME;
//		junit.framework.Test suite = Junit3PropertyTestSuite.suite();
//		int testCaseCount = suite.countTestCases();
//		assertEquals("Expected different number of testcases", 3, testCaseCount);
//		MutationProperties.TEST_CLASSES = null;
//	}
//
//	@Test
//	public void testMethodProperty() {
//		MutationProperties.TEST_METHODS = TEST_CLASS_NAME + ".test1:"
//				+ TEST_CLASS_NAME + ".test2";
//		junit.framework.Test suite = Junit3PropertyTestSuite.suite();
//		int testCaseCount = suite.countTestCases();
//		assertEquals("Expected different number of testcases", 2, testCaseCount);
//		MutationProperties.TEST_METHODS = null;
//	}
//
//	@Test
//	public void testNoProp() {
//		MutationProperties.TEST_METHODS = null;
//		MutationProperties.TEST_CLASSES = null;
//		try {
//			Junit3PropertyTestSuite.suite();
//			fail("Expected exception");
//		} catch (IllegalStateException e) {
//			// expected
//		}
//	}
//
//	@Test
//	public void testNoExceptionClassNotFound() {
//		MutationProperties.TEST_CLASSES = "NonExisitingClass.m1";
//		MutationProperties.TEST_METHODS = null;
//		Junit3PropertyTestSuite.suite();
//		MutationProperties.TEST_CLASSES = null;
//		MutationProperties.TEST_METHODS = "NonExisitingClass.m1";
//		Junit3PropertyTestSuite.suite();
//		MutationProperties.TEST_METHODS = null;
//	}
// }
