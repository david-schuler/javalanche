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
package de.unisb.cs.st.javalanche.mutation.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class Junit3TestRunner {

	private static final String JAVALANCHE_TESTSUITE = "javalanche.testsuite";

	public static void runTests(Class<? extends TestCase> tests) {
		Test suite = null;
		Method suiteMethod = null;
		try {
			suiteMethod = tests.getMethod("suite", new Class[0]);
		} catch (NoSuchMethodException e) {
		}
		// http://www.koders.com/java/fid38AD93C7350B38A5420CFE1EEEA588316BE3E8BF.aspx?s=JUnitTestRunner#L68
		if (suiteMethod != null) {
			try {
				suite = (Test) suiteMethod.invoke(null, new Class[0]);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			suite = new TestSuite(tests);
		}
		TestResult result = new TestResult();
		if (suite != null) {
			suite.run(result);
		} else {	
			throw new RuntimeException("Could not generate testsuite");
		}
		System.out.println(result.runCount());
	}

	@SuppressWarnings("unchecked")
	public static void runTests(String className) throws ClassNotFoundException {
		Class<? extends TestCase> testClass = (Class<? extends TestCase>) Class
				.forName(className);
		runTests(testClass);

	}

	public static void main(String[] args) throws ClassNotFoundException {
		// runTests("de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses.ArithmeticTestSuite");
		String property = System.getProperty(JAVALANCHE_TESTSUITE);
		if (property != null) {
			try {
				runTests(property);
			} catch (ClassNotFoundException e) {
				System.err.println("Testsuite does not exist" + property);
				e.printStackTrace();
			}
		} else {
			System.err.println("You have to specify the property "
					+ JAVALANCHE_TESTSUITE);
		}
	}

}
