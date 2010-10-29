package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3Suite;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3TestCase2;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit4Suite;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit4TestCase2;

public class JavalancheWrapperTestSuiteTest {

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


	private void testHelper(Class<?> testClass, int expecedTests) {
		String back = MutationProperties.TEST_SUITE;
		MutationProperties.TEST_SUITE = testClass.getName();
		JavalancheWrapperTestSuite javalancheWrapperTestSuite = new JavalancheWrapperTestSuite(
				null);
		RunNotifier notifier = new RunNotifier();
		TestCounter tc = new TestCounter();
		notifier.addListener(tc);
		javalancheWrapperTestSuite.run(notifier);
		assertEquals(expecedTests, tc.getNumberOfTests());
		MutationProperties.TEST_SUITE = back;
	}

	@Test
	public void testJunit3Test() {
		testHelper(Junit3TestCase2.class, 3);
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
	public void testJunit4Suite() {
		testHelper(Junit4Suite.class, 7);
	}
}

