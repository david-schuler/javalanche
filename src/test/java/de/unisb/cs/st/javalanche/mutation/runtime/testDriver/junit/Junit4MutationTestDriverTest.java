package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
//import static org.junit.matchers.JUnitMatchers.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.SuiteMethod;
import org.junit.runner.Description;
import org.junit.runner.Runner;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.AllTestsJunit3;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.DebugTestClass;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3SuiteMultipleTestsSameName;

public class Junit4MutationTestDriverTest {

	private Junit4MutationTestDriver driver;

	@Before
	public void setUp() {
		MutationProperties.TEST_SUITE = AllTestsJunit3.class.getName();
		driver = new Junit4MutationTestDriver();

	}

	@Test
	public void testSize() {
		List<String> allTests = driver.getAllTests();
		assertThat(allTests.size(), is(3));
	}

	@Test
	public void testPassingTest() {
		runTestHelper("testPass", TestOutcome.PASS);
	}

	@Test
	public void testFailingTest() {
		runTestHelper("testFail", TestOutcome.FAIL);
	}

	@Test
	public void testErrorTest() {
		runTestHelper("testError", TestOutcome.ERROR);
	}

	@Test
	public void testFailureMessage() {
		MutationProperties.IGNORE_MESSAGES = false;
		SingleTestResult result2 = runTestHelper("testFail", TestOutcome.FAIL);
		String message = result2.getTestMessage().getMessage();
		assertThat(message, containsString(DebugTestClass.FAILURE_MESSAGE));
	}

	@Test
	public void testErrorMessage() {
		MutationProperties.IGNORE_MESSAGES = false;
		SingleTestResult result2 = runTestHelper("testError", TestOutcome.ERROR);
		String message = result2.getTestMessage().getMessage();
		System.out.println("MESSAGE " + message);
		assertThat(message, containsString(DebugTestClass.ERROR_MESSAGE));
	}

	@Test
	public void testErrorMessageWithTrace() {
		MutationProperties.IGNORE_EXCEPTION_TRACES = false;
		SingleTestResult result2 = runTestHelper("testError", TestOutcome.ERROR);
		String message = result2.getTestMessage().getMessage();
		String trace1 = DebugTestClass.class.getCanonicalName();
		String trace2 = Junit4MutationTestDriverTest.class.getCanonicalName();
		assertThat(message, containsString(trace1));
		assertThat(message, containsString(trace2));
	}

	private SingleTestResult runTestHelper(String testName, TestOutcome expected) {
		List<String> allTests = driver.getAllTests();
		String test = getElement(allTests, testName);
		MutationTestRunnable runnable = driver.getTestRunnable(test);
		runnable.run();
		SingleTestResult result = runnable.getResult();
		TestOutcome outcome = result.getOutcome();
		assertThat(outcome, is(expected));
		return result;
	}

	private String getElement(List<String> allTests, String testName) {
		for (String string : allTests) {
			if (string.contains(testName)) {
				return string;
			}
		}
		return null;
	}
}
