package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * An example mutation listener that prints the events to System.out.
 * 
 * @author David Schuler
 * 
 */
public class PrintListener implements MutationTestListener {

	/**
	 * Creates a new PrintListener.
	 */
	public PrintListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #mutationStart(de.unisb.cs.st.javalanche.mutation.results.Mutation)
	 */
	public void mutationStart(Mutation mutation) {
		print("mutation start - " + mutation.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #mutationEnd(de.unisb.cs.st.javalanche.mutation.results.Mutation)
	 */
	public void mutationEnd(Mutation mutation) {
		print("mutation end - " + mutation.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #testEnd(java.lang.String)
	 */
	public void testEnd(String testName) {
		print("test end - " + testName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #testStart(java.lang.String)
	 */
	public void testStart(String testName) {
		print("test start - " + testName);
	}

	/**
	 * Prints given message to System.out.
	 * 
	 * @param message
	 *            the message to print
	 */
	private void print(String message) {
		System.out.println("PrintListener: " + message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #end()
	 */
	public void end() {
		print("Mutation test start");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #start()
	 */
	public void start() {
		print("Mutation test end");
	}
}
