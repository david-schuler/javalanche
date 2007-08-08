package org.softevo.mutation.testsuite;

import junit.framework.TestResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.hibernate.HibernateTest;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.MutationManager;

public class TestResultReporter {

	ResultReporter reporter = new ResultReporter();

	@Before
	public void setUp() {
		TestResult tr1 = new TestResult();
		TestResult tr2 = new TestResult();
		junit.framework.Test test = new junit.framework.Test() {

			public int countTestCases() {
				return 2;
			}

			public void run(TestResult result) {

			}
		};
		tr1.addError(test, null);
		tr1.addFailure(test, null);
		tr1.startTest(test);

		tr2.addError(test, null);
		tr2.addError(test, null);
		tr2.addFailure(test, null);
		tr2.addFailure(test, null);
		tr2.startTest(test);

		Mutation m = new Mutation("testClass", 1,
				MutationType.RIC_PLUS_1);
		MutationManager.shouldApplyMutation(m);
		reporter.report(tr1,  m);
	}

	@After
	public void tearDown(){
		new HibernateTest().hibernateDelete();

	}



	@Test
	public void testToString() {
		@SuppressWarnings("unused")
		String s = reporter.toString();

	}

}
