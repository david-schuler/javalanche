package de.unisb.cs.st.javalanche.mutation.runtime.testsuites;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.Junit3MutationTestDriver;

/**
 * Subclass of JUnits {@link TestSuite} class. It is used to execute the tests
 * for the mutated program. It repeatedly executes the test-cases for every
 * mutation, but only executes the tests that cover the mutation.
 *
 * @author David Schuler
 *
 */
public class MutationTestSuite extends TestSuite {

	private static Logger logger = Logger.getLogger(MutationTestSuite.class);

	/**
	 * Creates a new MutationTestSuite with the given name.
	 *
	 * @param name
	 *            the name of the test suite
	 *
	 */
	public MutationTestSuite(String name) {
		super(name);
	}

	/**
	 * Creates a new MutationTestSuite with no name.
	 */
	public MutationTestSuite() {
		super();
	}

	/**
	 * Delegates the control to {@link Junit3MutationTestDriver}.
	 *
	 * @see junit.framework.TestSuite#run(junit.framework.TestResult)
	 *
	 */
	@Override
	public void run(TestResult result) {
		Junit3MutationTestDriver driver = new Junit3MutationTestDriver(this);
		driver.run();
	}

	/**
	 * Transforms a {@link TestSuite} to a MutationTestSuite. This method is
	 * called by instrumented code to insert this class instead of the
	 * TestSuite.
	 *
	 * @param testSuite
	 *            The original TestSuite.
	 * @return The MutationTestSuite that contains the given TestSuite.
	 */
	public static MutationTestSuite toMutationTestSuite(TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		MutationTestSuite returnTestSuite = new MutationTestSuite(testSuite
				.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
