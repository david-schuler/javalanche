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

public interface JavalancheConfiguration {

	/**
	 * Returns true when whether mutations of given type should be enabled.
	 * 
	 * @param t
	 *            the mutation type to check.
	 * @return true, when whether mutations of given type should be enabled.
	 */
	boolean enableMutationType(MutationType t);

	/**
	 * Returns the timeout for one mutation in seconds.
	 * 
	 * @return the timeout for one mutation in seconds.
	 */
	int getTimeoutInSeconds();

	/**
	 * Returns a string of test names that should be excluded from mutation
	 * testing. TODO
	 * 
	 * @return
	 */
	String getExcludedTests();

	File getExcludeFile();

	/**
	 * internally
	 * 
	 * @see String.matches is invoked on classnames
	 * @return
	 */
	String getIgnorePattern();

	/**
	 * Returns only the filename of the task file
	 * 
	 * @return
	 */
	// TODO make it complete file
	File getMutationIdFile();

	File getOutputDir();

	String getProjectPrefix();

	String getProjectSourceDir();

	RunMode getRunMode();

	int getSaveInterval();

	File getTestExcludeFile();

	File getTestMap();

	String getTestNames();

	int getTestPermutations();

	boolean stopAfterFirstFail();

	boolean storeTestMessages();

	boolean storeTraces();

	boolean useThreadStop();

}
