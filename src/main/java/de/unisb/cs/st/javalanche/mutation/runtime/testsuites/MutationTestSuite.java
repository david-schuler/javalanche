/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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
		logger.info("Running mutation test suite");
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
