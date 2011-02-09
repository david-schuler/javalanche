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

/**
 * Interface to integrate different test architectures into the mutation testing
 * system.
 *
 * @author David Schuler
 *
 */
public interface MutationTestRunnable extends Runnable {

	/**
	 * Returns true, if the test has finished.
	 * 
	 * @return true,if the test has finished
	 */
	public boolean hasFinished();

	/**
	 * Returns the results of this test.
	 *
	 * @return the results of this test
	 */
	public SingleTestResult getResult();

	/**
	 * Method is called by {@link MutationTestDriver} when the test failed.
	 * 
	 * @param message
	 *            a message that describes why the test failed
	 */
	public void setFailed(String message, Throwable e);

}
