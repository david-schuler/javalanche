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
package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationMX implements MutationMXMBean {

	private List<Long> mutations = new ArrayList<Long>();

	private String currentTest;

	private Mutation currentMutation;

	private StopWatch mutationStopWatch = new StopWatch();

	private StopWatch testStopWatch = new StopWatch();

	public void setTest(String testName) {
		testStopWatch.reset();
		testStopWatch.start();
		currentTest = testName;
	}

	public void addMutation(Mutation mutation) {
		mutationStopWatch.reset();
		mutationStopWatch.start();
		currentMutation = mutation;
		mutations.add(mutation.getId());
	}

	public String getCurrentTest() {
		return currentTest;
	}

	public long getTestDuration() {
		return testStopWatch.getTime();
	}

	public String getMutations() {
		return Joiner.on(',').join(mutations);
	}

	public int getNumberOfMutations() {
		return mutations.size();
	}

	public long getMutationDuration() {
		return mutationStopWatch.getTime();
	}

	public String getCurrentMutation() {
		if (currentMutation != null) {
			return currentMutation.toShortString();
		}
		return "";
	}

	public String getMutationSummary() {
		MutationsForRun instance = MutationsForRun.getFromDefaultLocation();
		List<Mutation> mutationListInstance = instance.getMutations();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		if (MutationProperties.MUTATION_FILE_NAME != null) {
			File file = new File(MutationProperties.MUTATION_FILE_NAME);
			if (file.exists()) {
				// TODO mutationList = QueryManager.getMutationsByFile(file);
			}
		}
//		int withResult = 0;
//		long totalDuration = 0;
//		for (Mutation mutation : mutationList) {
//			if (mutation.getMutationResult() != null) {
//				withResult++;
//				MutationTestResult mutationResult = mutation
//						.getMutationResult();
//				Collection<TestMessage> allTestMessages = mutationResult
//						.getAllTestMessages();
//				long duration = 0;
//				for (TestMessage tm : allTestMessages) {
//					duration += tm.getDuration();
//				}
//				totalDuration += duration;
//			}
//		}
		return String.format("Out of %d mutations of ///  this task %%d got results (Run for %%s). Mutations for this run %d.",
				mutationList.size(), /*withResult, DurationFormatUtils.formatDurationHMS(totalDuration), */ mutationListInstance.size());
	}
}
