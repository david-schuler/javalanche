package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Join;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class MutationMX implements MutationMXMBean {

	private List<Mutation> mutations = new ArrayList<Mutation>();

	private String currentTest;

	public void setTest(String testName) {
		currentTest = testName;
	}

	public void addMutation(Mutation mutation) {
		mutations.add(mutation);
	}

	public String getCurrentTest() {
		return currentTest;
	}

	public String getMutations() {
		return Join.join("," , mutations);
	}

	public int getNumberOfMutations() {
		return mutations.size();
	}

}
