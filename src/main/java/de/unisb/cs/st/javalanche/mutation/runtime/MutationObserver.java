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
package de.unisb.cs.st.javalanche.mutation.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * Class used by the mutation test driver at runtime to report the testcases
 * that cover a mutation.
 * 
 * @author David Schuler
 * 
 */
public class MutationObserver implements MutationTestListener {

	private static Logger logger = Logger.getLogger(MutationObserver.class);

	/**
	 * All mutations that were activated.
	 */
	private static List<Mutation> reportedMutations = new ArrayList<Mutation>();

	/**
	 * All tests that where touched by tests.
	 */
	private static Collection<String> touchingTestCases = new HashSet<String>();

	/**
	 * Id of the currently active mutation.
	 */
	private static AtomicLong expectedID = new AtomicLong();

	/**
	 * Name of the currently active test.
	 */
	private static AtomicReference<String> actualTestCase = new AtomicReference<String>();

	/**
	 * Set of all mutations that where touched during this run.
	 */
	private static Set<Mutation> touchedMutations = new HashSet<Mutation>();

	/**
	 * Currently active mutation.
	 */
	private static AtomicReference<Mutation> actualMutation = new AtomicReference<Mutation>();

	/**
	 * Indicates whether the currently active Mutation was touched or not.
	 */
	private static AtomicBoolean touched = new AtomicBoolean();

	private static long time;

	/**
	 * Contains all mutations that are reported to have been applied.
	 */
	private final static Collection<Mutation> appliedMutations = new HashSet<Mutation>();

	public static final int LIMIT = MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS * 1000;

	/**
	 * This method is called by statements that are added to the mutated code.
	 * It is called every time the mutated statements get executed.
	 * 
	 * @param mutationID
	 *            the id of the mutation that is executed
	 */
	public static void touch(long mutationID) {
		// (actualMutation == null ? "null " : actualMutation.getId() + ""));
		if (mutationID != expectedID.get()) {
			String message = "Expected ID did not match reported ID "
					+ expectedID.get() + "  - " + mutationID;
			logger.warn(message);
			throw new RuntimeException(message);
		} else {

			touchingTestCases.add(actualTestCase.get());
			if (!touched.get()) {
				touchedMutations.add(actualMutation.get());
				logger.info("Touch called by mutated code in test: "
						+ actualTestCase + " for mutation: " + mutationID
						+ " Thread " + Thread.currentThread()
						+ " loaded by class loader "
						+ MutationObserver.class.getClassLoader());
				// + "Trace " + Util.getStackTraceString());
				touched.set(true);
				time = System.currentTimeMillis();
			} else {
				if (System.currentTimeMillis() - time > LIMIT) {
					throw new RuntimeException(MutationProperties.MUTATION_TIME_LIMIT_MESSAGE);
				}
			}
		}
	}

	public static void mutationApplied(Mutation m) {
		appliedMutations.add(m);
	}

	/**
	 * Returns a summary for all collected test outcomes and writes these to a
	 * file if a property for the file name was set.
	 * 
	 * @return String containing the summary.
	 */
	public static String summary(boolean finishedNormal) {
		RunResult runResult = new RunResult(reportedMutations,
				touchedMutations, appliedMutations, finishedNormal);
		return runResult.toString();
	}

	/**
	 * Return all test cases that touched the currently active mutation up to
	 * this point.
	 * 
	 * @return the test cases that touched the currently active mutation
	 */
	public static Collection<String> getTouchingTestCases() {
		return touchingTestCases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #end()
	 */
	public void end() {
	}

	/**
	 * Called when a mutation starts. Resets the according variables.
	 * 
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#mutationStart(de.unisb.cs.st.javalanche.mutation.results.Mutation)
	 */
	public void mutationStart(Mutation mutation) {
		logger.info("Mutation start");
		reportedMutations.add(mutation);
		actualMutation.set(mutation);
		touchingTestCases.clear();
		expectedID.set(mutation.getId());
		touched.set(false);
		// shouldEnd.set(false);
	}

	/**
	 * Not used
	 */
	public void mutationEnd(Mutation mutation) {
	}

	/**
	 * Not used
	 */
	public void start() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #testEnd(java.lang.String)
	 */
	public void testEnd(String testName) {
		actualTestCase.set(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener
	 * #testStart(java.lang.String)
	 */
	public void testStart(String testName) {
		actualTestCase.set(testName);
	}

	/**
	 * Called at the end of mutation testing. Prints a message, and an error
	 * message if one or more mutations where not applied.
	 * 
	 */
	public static void reportAppliedMutations() {
		List<Mutation> notApplied = new ArrayList<Mutation>();
		List<Mutation> mutations = MutationsForRun
				.getFromDefaultLocation(false).getMutations();
		int applied = 0;
		List<Long> appliedIds = new ArrayList<Long>();
		for (Mutation m : appliedMutations) {
			appliedIds.add(m.getId());
		}
		for (Mutation m : mutations) {
			if (appliedIds.contains(m.getId())) {
				applied++;
			} else {
				notApplied.add(m);
			}
		}
		logger.info(applied + " Mutations out of " + mutations.size()
				+ " where applied to bytecode");
		if (applied < mutations.size() || notApplied.size() > 0) {
			logger.error("Not all mutations where applied to bytecode");
			logger.error(appliedMutations);
			logger.error(mutations);
			for (Mutation mutation : notApplied) {
				logger.warn("Mutation not applied " + mutation.getId());
			}
		}
	}

}
