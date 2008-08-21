package org.softevo.mutation.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;

public class MutationResultAnalyzer implements MutationAnalyzer {

	private static final boolean WRITE_FILES = false;

	public String analyze(Iterable<Mutation> mutations) {
		int killed = 0;
		int survived = 0;
		int classInit = 0;
		int notCovered = 0;
		List<Mutation> killedList = new ArrayList<Mutation>();
		List<Mutation> survivedList = new ArrayList<Mutation>();
		List<Mutation> survivedTouchedList = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation == null) {
				throw new RuntimeException("Null fetched from db");
			}
			MutationTestResult mutationResult = mutation.getMutationResult();

			if (mutationResult != null
					&& (mutationResult.getNumberOfErrors() > 0 || mutationResult
							.getNumberOfFailures() > 0)) {
				killed++;
				killedList.add(mutation);
			} else {
				survived++;
				survivedList.add(mutation);
				if(mutationResult!= null){
					survivedTouchedList.add(mutation);
				}
			}
			if (mutationResult == null) {
				notCovered++;
			}
			if (mutation.isClassInit()) {
				classInit++;
			}

		}
		int total = survived + killed;
		StringBuilder sb = new StringBuilder();

		Set<Long> killedIds = new TreeSet<Long>();
		Set<Long> survivedIds = new TreeSet<Long>();
		for (Mutation mutation : killedList) {
			killedIds.add(mutation.getId());
		}
		for (Mutation mutation : survivedTouchedList) {
			survivedIds.add(mutation.getId());
		}
		if (WRITE_FILES) {
			XmlIo.toXML(killedList, "killed-mutations.xml");
			XmlIo.toXML(survivedList, "survived-mutations.xml");
			XmlIo.toXML(killedIds, "killed-ids.xml");
			XmlIo.toXML(survivedIds, "survived-ids.xml");
		}
		sb.append("Total mutations:  " + total);
		sb.append('\n');
		sb.append("Mutations with results: " + (total - notCovered) + "  ("
				+ AnalyzeUtil.formatPercent((double) (total - notCovered) / total) + ")");
		sb.append('\n');
		sb.append("Killed mutations: " + killed);
		sb.append('\n');
		sb.append(String.format("Survived mutations: %d (%s) IDs:\n" , survived , AnalyzeUtil.formatPercent(survived,total)));
		for (Long survivedId : survivedIds) {
			sb.append(survivedId).append(", ");
		}
		sb.append('\n');
		sb
				.append("Mutation score: "
						+ AnalyzeUtil.formatPercent(((double) killed) / total));
		sb.append('\n');
		sb.append("Mutation score for mutations that were covered: "
				+ AnalyzeUtil.formatPercent((double)killed / (total - notCovered)));
		sb.append('\n');
		sb.append("Mutations that were not covered: " + notCovered + "  ("
				+ AnalyzeUtil.formatPercent((double) (notCovered) / total )+ ")");
		sb.append('\n');
		sb.append("Mutations that can not be killed: " + classInit);
		sb.append('\n');
		return sb.toString();
	}

}
