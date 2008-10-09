package de.st.cs.unisb.javalanche.run.analyze;

import java.util.List;

import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationTestResult;
import de.st.cs.unisb.javalanche.results.TestMessage;

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
