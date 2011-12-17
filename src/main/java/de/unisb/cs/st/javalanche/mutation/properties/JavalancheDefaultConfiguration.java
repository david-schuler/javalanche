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

/**
 * Default configuration of Javalanche.
 * 
 * @author David Schuler
 * 
 */
public class JavalancheDefaultConfiguration implements JavalancheConfiguration {

	@Override
	public boolean enableMutationType(MutationType t) {
		switch (t) {
		case ARITHMETIC_REPLACE:
		case NEGATE_JUMP:
		case REMOVE_CALL:
		case REPLACE_CONSTANT:
			return true;
		case REPLACE_VARIABLE:
		case ABSOLUTE_VALUE:
		case UNARY_OPERATOR:
		case REPLACE_THREAD_CALL:
		case MONITOR_REMOVE:
			return false;
		}
		return false;
	}

	@Override
	public int getTimeoutInSeconds() {
		return 10;
	}

	@Override
	public String getExcludedTests() {
		return null;
	}

	@Override
	public File getExcludeFile() {
		return new File(getOutputDir(), "exclude.txt");
	}

	@Override
	public String getIgnorePattern() {
		return "";
	}

	@Override
	public File getMutationIdFile() {
		return null;
	}

	@Override
	public File getOutputDir() {
		return new File("./mutation-files");
	}

	@Override
	public String getProjectPrefix() {
		return null;
	}

	@Override
	public String getProjectSourceDir() {
		return null;
	}

	@Override
	public RunMode getRunMode() {
		return RunMode.OFF;
	}

	@Override
	public int getSaveInterval() {
		return 50;
	}

	@Override
	public File getTestExcludeFile() {
		return new File(getOutputDir(), "test-exclude.txt"); // TODO other dir
	}

	@Override
	public String getTestNames() {
		return null;
	}

	@Override
	public int getTestPermutations() {
		return 10;
	}

	@Override
	public boolean stopAfterFirstFail() {
		return true;
	}

	@Override
	public boolean storeTestMessages() {
		return false;
	}

	@Override
	public boolean storeTraces() {
		return false;
	}

	@Override
	public boolean useThreadStop() {
		return true;
	}

	@Override
	public boolean runAllTestsForMutation() {
		return false;
	}

	@Override
	public boolean useJunit3Runner() {
		return false;
	}

}
