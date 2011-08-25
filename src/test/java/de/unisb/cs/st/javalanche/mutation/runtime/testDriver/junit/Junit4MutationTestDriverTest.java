package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.model.InitializationError;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.AllTestsJunit3;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.DebugTestClass;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit4Suite;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.TestCaseForJunit4Test;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class Junit4MutationTestDriverTest {

	private Junit4MutationTestDriver driver;

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

	@Before
	public void setUp() {
		config.setTestNames(AllTestsJunit3.class.getName());
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
		config.setStoreTestMessages(true); // TODO test false
		SingleTestResult result2 = runTestHelper("testFail", TestOutcome.FAIL);
		String message = result2.getTestMessage().getMessage();
		assertThat(message, containsString(DebugTestClass.FAILURE_MESSAGE));
	}

	@Test
	public void testErrorMessage() {
		config.setStoreTestMessages(true); // TODO test false
		SingleTestResult result2 = runTestHelper("testError", TestOutcome.ERROR);
		String message = result2.getTestMessage().getMessage();
		System.out.println("MESSAGE " + message);
		assertThat(message, containsString(DebugTestClass.ERROR_MESSAGE));
	}

	@Test
	public void testErrorMessageWithTrace() {
		config.setStoreTraces(true); // TODO test false
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

	@Test
	public void testWithJunit4Suite() {
		config.setTestNames(Junit4Suite.class.getName());
		driver = new Junit4MutationTestDriver();
		runTestHelper("test3", TestOutcome.PASS);

	}


}
