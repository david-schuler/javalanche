package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

/**
 * Interface to integrate different test architectures into the mutation testing
 * system.
 *
 * @author David Schuler
 *
 */
public interface MutationTestRunnable extends Runnable {

	/**
	 * Returns true, if the test has finshed.
	 *
	 * @return true,if the test has finshed
	 */
	public boolean hasFinished();

	/**
	 * Returns the results of this test.
	 *
	 * @return the results of this test
	 */
	public SingleTestResult getResult();

	/**
	 * Signalize this runnable that it failed.
	 *
	 * @param message
	 *            a message that describes why the test failed
	 */
	public void setFailed(String message);

}
