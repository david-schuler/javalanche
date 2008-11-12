package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Interface that can be implemented to observe the mutation testing process.
 *
 * @author David Schuler
 *
 */
public interface MutationTestListener {

	/**
	 * This method is called everytime a new mutation is tested.
	 *
	 * @param mutation
	 *            the mutation that is currently tested
	 */
	void mutationStart(Mutation mutation);

	/**
	 * This method is called everytime the tests for a mutation end.
	 *
	 * @param mutation
	 *            the mutation that was tested
	 */
	void mutationEnd(Mutation mutation);

	/**
	 * This method is called before a single test is executed. This usually
	 * happens many times for a mutation.
	 *
	 * @param testName
	 *            the name of the test
	 */
	void testStart(String testName);

	/**
	 * This method is called after a single test was executed. This usually
	 * happens many times for a mutation.
	 *
	 * @param testName
	 *            the name of the test
	 */
	void testEnd(String testName);
}
