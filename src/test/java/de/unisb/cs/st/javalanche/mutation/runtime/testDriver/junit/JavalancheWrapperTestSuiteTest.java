package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3Suite;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3TestCase2;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit4Suite;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit4TestCase2;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunit4Test;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunitTest;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class JavalancheWrapperTestSuiteTest {

	private static final String TEST_CLASS_NAME_JUNIT3 = "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.AllTestsJunit3";

	private class TestCounter extends RunListener {
		private int tests = 0;

		@Override
		public void testFinished(Description description) throws Exception {
			tests++;
			super.testFinished(description);
		}

		public int getNumberOfTests() {
			return tests;
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	private static JavalancheConfiguration configBack;
	private static JavalancheTestConfiguration config;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
	}

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}

	private Runner testHelper(Class<?> testClass, int expecedTests) {
		return testHelper(new Class<?>[] { testClass }, expecedTests);
	}

	private Runner testHelper(Class<?>[] testClasses, int expecedTests) {
		config.setTestNames(getTestNames(testClasses));
		JavalancheWrapperTestSuite javalancheWrapperTestSuite = new JavalancheWrapperTestSuite(
				null);
		RunNotifier notifier = new RunNotifier();
		TestCounter tc = new TestCounter();
		notifier.addListener(tc);
		int testCount = javalancheWrapperTestSuite.testCount();
		// javalancheWrapperTestSuite.run(notifier);
		// assertEquals(expecedTests, tc.getNumberOfTests());
		assertEquals(expecedTests, testCount);
		return javalancheWrapperTestSuite;
	}

	private String getTestNames(Class<?>[] testClasses) {
		List<String> names = new ArrayList<String>();
		for (Class<?> testClass : testClasses) {
			names.add(testClass.getName());
		}
		return Joiner.on(':').join(names);
	}

	@Test
	public void testJunit3Test() {
		testHelper(Junit3TestCase2.class, 3);
	}

	@Test
	public void testMultipleJunit3Test() throws Exception {
		try {
			testHelper(new Class<?>[] { Junit3TestCase2.class,
					TestCaseForJunitTest.class }, 6);
		} catch (Throwable t) {
			System.out
					.println("This test fails when junit < 4.8.2 is on the classpath.");
			System.out.println(t.getMessage());
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	@Test
	public void testJunit3Suite() {
		testHelper(Junit3Suite.class, 6);
	}

	@Test
	public void testJunit4Test() {
		testHelper(Junit4TestCase2.class, 4);
	}

	@Test
	public void testMultipleJunit4Test() {
		testHelper(new Class<?>[] { Junit4TestCase2.class,
				TestCaseForJunit4Test.class }, 7);
	}

	@Test
	public void testJunit4Suite() {
		Runner runner = testHelper(Junit4Suite.class, 7);
		// TODO: TEST for test classname and methodname
	}

	@Test
	public void testMethodPropertyJunit3() {
		// MutationProperties.TEST_METHODS = "true";
		// String p =
		// "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.";
		// MutationProperties.TEST_SUITE = p + "DebugTestClass" + ".testObject";
		// // ":" +p+"AllTestsJunit3";
		// // JavalancheWrapperTestSuite javalancheWrapperTestSuite = new
		// // JavalancheWrapperTestSuite(
		// // null);
		// // int testCaseCount = javalancheWrapperTestSuite.testCount();
		// // assertEquals("Expected different number of testcases", 2,
		// // testCaseCount);
		// Junit4MutationTestDriver driver = new Junit4MutationTestDriver();
		//
		// MutationProperties.TEST_METHODS = null;
		// MutationProperties.TEST_SUITE = null;
		// TODO Reintroduce Test methods or delete
	}

}
