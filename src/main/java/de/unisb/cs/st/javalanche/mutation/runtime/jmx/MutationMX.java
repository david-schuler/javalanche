package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import com.google.common.base.Join;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

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
		return Join.join(",", mutations);
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
		MutationForRun instance = MutationForRun.getInstance();
		List<Mutation> mutationListInstance = instance.getMutations();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		if (MutationProperties.MUTATION_FILE_NAME != null) {
			File file = new File(MutationProperties.MUTATION_FILE_NAME);
			if (file.exists()) {
				mutationList = MutationForRun.getMutationsByFile(file);
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
