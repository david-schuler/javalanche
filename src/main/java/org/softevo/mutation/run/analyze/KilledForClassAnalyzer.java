package org.softevo.mutation.run.analyze;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.QueryManager;

public class KilledForClassAnalyzer implements MutatedUnmutatedAnalyzer {

	private Map<String, MutationsClassData> mutationData = new HashMap<String, MutationsClassData>();

	public String getResults() {
		for(MutationsClassData m : mutationData.values()){
			m.setMutationsTotal(QueryManager.getNumberOfMutationsForClass(m.getClassName()));
		}
		XmlIo.toXML(mutationData, new File("mutations-class-result.xml"));
		return "";
	}

	public void handleMutation(Mutation mutated, Mutation unMutated) {

		MutationsClassData mutationsClassData = null;
		String key = mutated.getClassName();
		if (mutationData.containsKey(key)) {
			mutationsClassData = mutationData.get(key);
		} else {
			mutationsClassData = new MutationsClassData(key);
			mutationData.put(key, mutationsClassData);
		}

		SingleTestResult mutatedResult = mutated.getMutationResult();
		SingleTestResult unMutatedResult = unMutated.getMutationResult();

		int unMutatedFailures = unMutatedResult.getNumberOfFailures();
		int unMutatedErrors = unMutatedResult.getNumberOfErrors();
		// int unMutatedRuns = unMutatedResult.getRuns();
		int unMutatedTotalFailing = unMutatedFailures + unMutatedErrors;

		int mutatedFailures = mutatedResult.getNumberOfFailures();
		int mutatedErrors = mutatedResult.getNumberOfErrors();
		// int mutatedRuns = mutatedResult.getRuns();
		int mutatedTotalFailing = mutatedFailures + mutatedErrors;

		if (mutatedTotalFailing > unMutatedTotalFailing) {
			mutationsClassData.addMutationKilled();
		} else {
			mutationsClassData.addMutationSurvived();
		}

	}


}
