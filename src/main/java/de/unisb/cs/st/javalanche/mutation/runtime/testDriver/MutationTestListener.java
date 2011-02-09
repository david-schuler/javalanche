/*
* Copyright (C) 2011 Saarland University
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
	 * This method is called once before the mutation testing
	 */
	void start();

	/**
	 * This method is called once after the mutation testing
	 */
	void end();

	/**
	 * This method is called every time a new mutation is tested.
	 * 
	 * @param mutation
	 *            the mutation that is currently tested
	 */
	void mutationStart(Mutation mutation);

	/**
	 * This method is called every time the tests for a mutation end.
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
