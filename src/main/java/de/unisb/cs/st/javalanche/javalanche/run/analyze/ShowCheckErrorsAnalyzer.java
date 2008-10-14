package de.unisb.cs.st.javalanche.mutation.run.analyze;

import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class ShowCheckErrorsAnalyzer implements MutatedUnmutatedAnalyzer {

	public String getResults() {
		return null;
	}

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		MutationTestResult res = mutated.getMutationResult();
		if (res != null) {
			if (res.getNumberOfErrors() != 0 || res.getNumberOfFailures() != 0) {
				for (TestMessage tm : res.getErrors()) {
					if (!tm.isHasTouched()) {
						System.out.println(tm);
						System.out.println(unMutated);
					}
				}
				for (TestMessage tm : res.getFailures()) {
					if (!tm.isHasTouched()) {
//						System.out.println(tm);
					}
				}
			} else {
				List<TestMessage> l = res.getPassing();
				for (TestMessage tm : l) {
					if (tm.isHasTouched()) {
					}
				}
			}
		}
	}

}
