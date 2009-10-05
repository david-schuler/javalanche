/*
* Copyright (C) 2009 Saarland University
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
 * Shutdown hook that is used during mutation testing. In case of an unexpected
 * shutdown this hook is activated. Most likely this will be an endless loop
 * caused by a mutation.
 *
 * @author David Schuler
 *
 */
public class MutationDriverShutdownHook implements Runnable {

	private final MutationTestDriver mtd;

	/**
	 * Creates a knew ShutdownHook for the given {@link MutationTestDriver}.
	 * 
	 * @param mtd
	 *            the mutation test driver the shutdown hook is created for
	 */
	public MutationDriverShutdownHook(MutationTestDriver mtd) {
		this.mtd = mtd;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		mtd.unexpectedShutdown();
	}

}
