package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class PrintListener implements MutationTestListener {

	public PrintListener() {
	}

	public void mutationStart(Mutation mutation) {
		print("mutation start - " + mutation.getId());
	}

	public void mutationEnd(Mutation mutation) {
		print("mutation end - " + mutation.getId());
	}

	public void testEnd(String testName) {
		print("test end - " + testName);
	}

	public void testStart(String testName) {
		print("test start - " + testName);
	}

	private void print(String message) {
		System.out.println("PrintListener: " + message);
	}

}
