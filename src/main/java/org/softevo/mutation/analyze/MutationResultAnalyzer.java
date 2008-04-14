package org.softevo.mutation.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;

public class MutationResultAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations) {
		int killed = 0;
		int survived = 0;
		List<Mutation> killedList = new ArrayList<Mutation>();
		List<Mutation> survivedList = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			SingleTestResult mutationResult = mutation.getMutationResult();
			if (mutationResult.getNumberOfErrors() > 0
					|| mutationResult.getNumberOfFailures() > 0) {
				killed++;
				killedList.add(mutation);
			} else {
				survived++;
				survivedList.add(mutation);
			}
		}
		int total = survived + killed;
		StringBuilder sb = new StringBuilder();

		Set<Long> killedIds = new TreeSet<Long>();
		Set<Long> survivedIds = new TreeSet<Long>();
		for (Mutation mutation : killedList) {
			killedIds.add(mutation.getId());
		}
		for (Mutation mutation : survivedList) {
			survivedIds.add(mutation.getId());
		}
		XmlIo.toXML(killedList, "killed-mutations.xml");
		XmlIo.toXML(survivedList, "survived-mutations.xml");
		XmlIo.toXML(killedIds, "killed-ids.xml");
		XmlIo.toXML(survivedIds, "survived-ids.xml");



		sb.append("Total mutations:  " + total);
		sb.append('\n');
		sb.append("Killed mutations: " + killed);
		sb.append('\n');
		sb.append("Survived mutations: " + survived);
		sb.append('\n');
		return sb.toString();
	}

}
