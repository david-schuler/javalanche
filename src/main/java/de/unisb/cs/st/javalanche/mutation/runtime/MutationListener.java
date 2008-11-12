package de.unisb.cs.st.javalanche.mutation.runtime;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public interface MutationListener {

	/**
	 * Thi method is called once befor the mutation testing starts.
	 */
	public void start();

	/**
	 * This method is called everytime a mutation is enabled.
	 *
	 * @param mutation
	 *            the currently enabled mutation
	 */
	public void mutationStart(Mutation mutation);

	/**
	 * This method is called everytime a mutation is disabled.
	 *
	 * @param mutation
	 *            the mutation that is disabled
	 */
	public void mutationEnd(Mutation mutation);

	/**
	 * This method is called everytime a new test is started. This can happen
	 * several times for one mutation.
	 *
	 * @param testName
	 *            the name of the test
	 */
	public void testStarted(String testName);

	/**
	 * This method is called everytime a test is finished. This can happen
	 * several times for one mutation.
	 *
	 * @param testName
	 *            the name of the test
	 */
	public void testEnd(String testName);

	/**
	 * This methd is called once when the mutation testing is finished.
	 */
	public void end();
}
