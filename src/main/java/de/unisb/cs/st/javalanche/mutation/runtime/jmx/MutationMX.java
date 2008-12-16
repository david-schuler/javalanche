package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class MutationMX implements MutationMXMBean {

	private boolean nextSafe;

	private List<Mutation> mutations = new ArrayList<Mutation>();

	private String currentTest;

	public int getConductedMutations() {
		System.out.println("My.getConductedMutations()");
		return mutations.size();
	}

	public boolean getNextSafe() {
		System.out.println("My.getNextSafe()");
		return nextSafe;
	}

	public void setNextSafe(boolean b) {
		nextSafe = b;
		System.out.println("My.setNextSafe()");
	}

	public void setTest(String testName) {
		currentTest = testName;
	}

	public void addMutation(Mutation mutation) {
		mutations.add(mutation);
	}

}
