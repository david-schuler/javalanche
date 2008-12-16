package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

public class JmxMutationTestListener implements MutationTestListener {

	private MutationMX bean;

	public void end() {
		MXBeanRegisterer.unregister(bean);
	}

	public void mutationEnd(Mutation mutation) {
	}

	public void mutationStart(Mutation mutation) {
		bean.addMutation(mutation);
	}

	public void start() {
		int runNumber = getRunNumber();
		bean = MXBeanRegisterer.registerMutationMXBean(runNumber);

	}

	private static int getRunNumber() {
		String run = "mutation-task-org_mozilla-27.txt";
		int start = run.lastIndexOf('-') + 1;
		int end = run.lastIndexOf(".txt");
		String numberString = run.substring(start, end);
		int result = Integer.parseInt(numberString);
		return result;
	}

	public void testEnd(String testName) {
	}

	public void testStart(String testName) {
		bean.setTest(testName);
	}

	public static void main(String[] args) {
		System.out.println(getRunNumber());
	}
}
