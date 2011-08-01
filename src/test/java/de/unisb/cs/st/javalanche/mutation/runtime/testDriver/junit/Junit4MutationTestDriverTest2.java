package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3RegularPlusSuite;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class Junit4MutationTestDriverTest2 {

	private MutationTestRunnable testRunnable;

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

	@Test
	public void test() throws Exception {

		String testName = Junit3RegularPlusSuite.class.getName();
		config.setTestNames(testName);
		Junit4MutationTestDriver j = new Junit4MutationTestDriver();

		List<String> allTests = j.getAllTests();
		assertEquals(2, allTests.size());
		System.out.println(allTests);
		String test1 = testName + ".test1";
		testRunnable = j.getTestRunnable(test1);
		Junit3RegularPlusSuite.check = 0;
		assertEquals(0, Junit3RegularPlusSuite.check);
		testRunnable.run();
		assertEquals(1, Junit3RegularPlusSuite.check);
		testRunnable = j.getTestRunnable(testName + ".test2");
		Junit3RegularPlusSuite.check = 0;
		testRunnable.run();
		assertEquals(2, Junit3RegularPlusSuite.check);

	}


}
