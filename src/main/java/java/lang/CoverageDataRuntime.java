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
package java.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to compute coverage data for mutations.
 * 
 * When this class is used by a test driver the method following methods have to
 * be called:
 * <ul>
 * <li>setTestName() at the beginning of every test.</li>
 * <li>unsetTestName() at the end of every test.</li>
 * <li>endCoverage() when the test suite has finished.</li>
 * </ul>
 * 
 * @author David Schuler
 * 
 */
public class CoverageDataRuntime {

	public String testName;

	static class SingletonHolder {
		final static CoverageDataRuntime instance = new CoverageDataRuntime();
	}

	/**
	 * Stores the coverage information. Maps a mutationId to an Set of tests.
	 */
	private Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();

	private Set<String> testsRun = new HashSet<String>();

	private CoverageDataRuntime() {
	}

	public static CoverageDataRuntime getInstance() {
		return SingletonHolder.instance;
	}

	static int call = 0;

	public static void touch(long id) {
		call++;
		// Only for debugging puprosses. Impacts performance
		// if (call % ((int) 1e6) == 0) {
		// System.out.println("CoverageDataRuntime.touch(): Touch called "
		// + call + "times.  Test "
		// + SingletonHolder.instance.getTestName()
		// + " touched mutation " + id);
		// }
		// System.out.println("CoverageDataRuntime.touch()" +
		// SingletonHolder.instance.testName.get());
		// + " touched mutation " + id);
		Set<String> coveredTests = SingletonHolder.instance.coverageData
				.get(id);
		if (coveredTests == null) {
			coveredTests = new HashSet<String>();
			SingletonHolder.instance.coverageData.put(id, coveredTests);
		}
		// boolean add =
		coveredTests.add(SingletonHolder.instance.getTestName());
		// if (add) {
		// System.out.println("Added test "
		// + SingletonHolder.instance.getTestName() + "for mutation "
		// + id);
		// }
	}

	private String getTestName() {
		// testName.get()
		return testName;
	}

	public static void setTestName(String testName) {
		CoverageDataRuntime instance = SingletonHolder.instance;
		instance.testsRun.add(testName);

		if (instance.getTestName() == null) {
			instance._setTestName(testName);
		} else {
			System.out
					.println("CoverageDataRuntime.setTestName() - Trying to overwrite testname");
			System.out
					.println("CoverageDataRuntime.setTestName() - Old testname: "
							+ instance.getTestName());
			System.out
					.println("CoverageDataRuntime.setTestName() - New testname: "
							+ testName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			System.out
					.println("CoverageDataRuntime.setTestName() - Stacktrace:\n"
							+ stackTraceString);
		}
	}

	public static void unsetTestName(String testName) {

		CoverageDataRuntime instance = SingletonHolder.instance;
		String oldTestName = instance.getTestName();
		if (oldTestName == null) {
			System.out
					.println("CoverageDataRuntime.setTestName() - Test name was  set to null expected "
							+ testName);
		} else if (oldTestName.equals(testName)) {
			instance._setTestName(null);
		} else {
			System.out
					.println("CoverageDataRuntime.setTestName() - Unset testname got different names");
			System.out
					.println("CoverageDataRuntime.setTestName() - Tried to unset: "
							+ testName);
			System.out
					.println("CoverageDataRuntime.setTestName() - but got currently acctive test name: "
							+ oldTestName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			System.out
					.println("CoverageDataRuntime.setTestName() - Stacktrace:\n"
							+ stackTraceString);
		}

	}

	private void _setTestName(String testName) {
		this.testName = testName;
	}

	public static void main(String[] args) {
		touch(23454904540l);
	}

	public static Set<String> getTestsRun() {
		return SingletonHolder.instance.testsRun;
	}

	public static Map<Long, Set<String>> getCoverageData() {
		return SingletonHolder.instance.coverageData;
	}

}
