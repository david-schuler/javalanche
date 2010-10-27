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
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import java.lang.adabu2.Tracer;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

public class AdabuListener implements MutationTestListener {

	private static final String JAVALANCHE_TEST_NAME = "javalanche.test.name";

	public void end() {
		System.out.println("AdabuListener.end()");
	}

	public void start() {
		System.out.println("AdabuListener.start()");
		Tracer.restart();
	}

	public void testEnd(String testName) {
		Tracer.restart();
		System.setProperty(JAVALANCHE_TEST_NAME, sanitizeTestName(testName));
	}

	public void mutationEnd(Mutation mutation) {

	}

	public void mutationStart(Mutation mutation) {

	}

	public void testStart(String testName) {
		System.setProperty(JAVALANCHE_TEST_NAME, sanitizeTestName(testName));
	}

	private String sanitizeTestName(String testName) {
		return testName.replace('/', '$');
	}

}
