package org.junit.internal.runners;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;

public class Junit38FilterTest {

	@Test(expected = NoTestsRemainException.class)
	public void testNoTestsRemainException() throws Exception {
			JUnit38ClassRunner runner = new JUnit38ClassRunner(
					Junit38Suite.suite());
		runner.filter(new Filter() {

			@Override
			public boolean shouldRun(Description description) {
				return false;
			}

			@Override
			public String describe() {
				return "test filter";
			}
		});
		
	}

	@Test
	public void testFilter() throws Throwable {
		// JUnit38ClassRunner runner = new
		// JUnit38ClassRunner(Junit38Suite.class);
		SuiteMethod runner = new SuiteMethod(Junit38Suite.class);
		runner.filter(new Filter() {

			@Override
			public boolean shouldRun(Description description) {
				String descClass = description.getClassName();
				String descMethod = description.getMethodName();
				System.out.println("DESC" + descClass + descMethod);
				boolean result = descClass != null && descMethod != null
						&& descClass.equals(Junit38Test.class.getName())
						&& descMethod.equals("test1");
				return result;
			}

			@Override
			public String describe() {
				return "test filter";
			}
		});
		assertEquals(1, runner.testCount());

	}

	@Test
	public void testFilter2() throws Throwable {
		// JUnit38ClassRunner(Junit38Suite.class);
		// JUnit38ClassRunner runner = new JUnit38ClassRunner(
		// WrappedJunit38TestSuite.class);
		SuiteMethod runner = new SuiteMethod(Junit38Suite.class);
		runner.filter(new Filter() {

			@Override
			public boolean shouldRun(Description description) {
				String descClass = description.getClassName();
				String descMethod = description.getMethodName();
				System.out.println("DESC" + descClass + descMethod);
				return descClass != null
						&& descMethod != null
						&& descClass.equals(Junit38Test.class.getName())
						&& (descMethod.equals("test1") || descMethod
								.equals("test2"));
			}

			@Override
			public String describe() {
				return "test filter";
			}
		});
		assertEquals(2, runner.testCount());

	}
}
