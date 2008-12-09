package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import java.lang.adabu2.Tracer;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class AdabuListener implements MutationTestListener {

	private static final String JAVALANCHE_TEST_NAME = "javalanche.test.name";

	public void end() {
		System.out.println("AdabuListener.end()");
	}

	public void start() {
		System.out.println("AdabuListener.start()");
		Tracer.restart();
	}

	public void testEnd(String testName) {
		Tracer.restart();
		System.setProperty(JAVALANCHE_TEST_NAME, sanitizeTestName(testName));
	}

	public void mutationEnd(Mutation mutation) {

	}

	public void mutationStart(Mutation mutation) {

	}

	public void testStart(String testName) {
		System.setProperty(JAVALANCHE_TEST_NAME, sanitizeTestName(testName));
	}

	private String sanitizeTestName(String testName) {
		return testName.replace('/', '$');
	}

}
