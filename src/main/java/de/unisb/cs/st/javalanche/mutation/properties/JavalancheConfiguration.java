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
package de.unisb.cs.st.javalanche.mutation.properties;

import java.io.File;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.Junit3MutationTestDriver;

/**
 * Class that supplies the the configuration for Javalanche.
 * 
 * @author David Schuler
 * 
 */
public interface JavalancheConfiguration {

	/**
	 * Returns true when whether mutations of given type should be enabled.
	 * 
	 * @param t
	 *            the mutation type to check.
	 * @return true, when whether mutations of given type should be enabled.
	 */
	boolean enableMutationType(MutationType t); // TODO rename method

	/**
	 * Returns the timeout for one mutation in seconds.
	 * 
	 * @return the timeout for one mutation in seconds.
	 */
	int getTimeoutInSeconds();

	/**
	 * Returns a string of test names that should be excluded from mutation
	 * testing. A name of the test consists of its fully classified class name
	 * and the name of the test method. For example:
	 * org.examlple.TestClass.testmethod
	 * 
	 * Multiple tests are separated by ':'. E.g.:
	 * org.examlple.TestClass.testmethod1:org.examlple.TestClass2.testmethod2
	 * 
	 * 
	 * @return a string of test names that should be excluded from mutation
	 *         testing.
	 */
	String getExcludedTests();

	/**
	 * Returns the file that contains the class names that are excluded from
	 * mutation testing.
	 * 
	 * @return the file of the excluded classes.
	 */
	File getExcludeFile();

	/**
	 * Returns a pattern for classes that are no mutated. The String.matches
	 * method is invoked for every class name with this pattern. If
	 * String.matches returns true, the class is not mutated.
	 * 
	 * @see String.matches
	 * 
	 * @return a pattern for classes that are no mutated.
	 */
	String getIgnorePattern();

	/**
	 * Returns a file that contains the ids of mutations that should be applied
	 * in this run.
	 * 
	 * @return a file that contains the ids of mutations that should be applied
	 *         in this run.
	 */
	File getMutationIdFile();

	/**
	 * Returns the directory where Javalanche writes its output files.
	 * 
	 * @return the directory where Javalanche writes its output files.
	 */
	File getOutputDir();

	/**
	 * Returns the package prefix of this project that gets mutation tested,
	 * that is the part of the package name that all classes that should be
	 * mutated have in common.
	 * 
	 * @return the package prefix of this project that gets mutation tested
	 */
	String getProjectPrefix();

	/**
	 * Returns the directory for the source files of this project. This method
	 * can also return null when no source directory was specified.
	 * 
	 * @return the source directory of this project.
	 */
	String getProjectSourceDir();

	/**
	 * Returns the current run mode of Javalanche.
	 * 
	 * @return the current run mode of Javalanche.
	 */
	RunMode getRunMode();

	/**
	 * Returns the save interval for which mutations are stored to the database,
	 * that is whenever as much mutations as specified vi the interval are
	 * executed the results are persisted.
	 * 
	 * @return save interval for which mutations are stored to the db.
	 */
	int getSaveInterval();

	/**
	 * Returns the name of the file that contains the test names (which are
	 * excluded from mutation testing).
	 * 
	 * @return the name of the file that contains the test names.
	 */
	File getTestExcludeFile();

	/**
	 * Return the names of the tests used for mutation testing this project.
	 * Multiple test names are separated by ':'.
	 * 
	 * @return the names of the tests.
	 */
	String getTestNames();

	/**
	 * Returns the number of permutations are used for checking the test suite.
	 * The test suite is executed in different order as many times as returned
	 * by this method and it is checked whether tests behave consistently.
	 * 
	 * @return the number of permutations are used for checking the test suite.
	 */
	int getTestPermutations();

	/**
	 * Returns true, when no further tests are executed for a mutation when
	 * after the first test fails.
	 * 
	 * @return true, when no further tests are executed for a mutation when
	 *         after the first test fails.
	 */
	boolean stopAfterFirstFail();

	/**
	 * Returns true, when the result messages of the tests for the mutations
	 * should be stored in the database.
	 * 
	 * @return true, when the test result messages should be stored.
	 */
	boolean storeTestMessages();

	/**
	 * Returns true, when the exception traces of the tests that fail on a
	 * mutation should be stored in the database.
	 * 
	 * @return true, when the exception traces of the failing tests should be
	 *         stored.
	 */
	boolean storeTraces();

	/**
	 * Returns true when the Thread.stop() method should be used to end threads
	 * of mutations that are in endless loops.
	 * 
	 * @return true, when the Thread.stop() method should be used.
	 */
	boolean useThreadStop();

	/**
	 * Returns true, when all test should be run instead of only those that
	 * cover the mutation.
	 * 
	 * @return true, when all test should be run instead of only those that
	 *         cover the mutation.
	 */
	boolean runAllTestsForMutation();

	/**
	 * Returns true, when {@link Junit3MutationTestDriver} should be used to run
	 * the tests.
	 * 
	 * @return true, when {@link Junit3MutationTestDriver} should be used to run
	 *         the tests.
	 */
	boolean useJunit3Runner();

}
