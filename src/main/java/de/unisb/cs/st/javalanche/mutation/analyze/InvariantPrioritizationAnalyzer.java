package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.AbstractPrioritizer.ScoreCalculator;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class InvariantPrioritizationAnalyzer implements MutationAnalyzer {

	private static final String INVARIANT_PRIORITIZATION_FILE_NAME = "invariantPrioritization.xml";

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Multimap<String, Mutation> mm = AnalyzeUtil
				.getDetectedByTest(mutations);
		List<String> invariantPrioritization = prioritize(mm);
		writePrioritization(invariantPrioritization,
				INVARIANT_PRIORITIZATION_FILE_NAME);
		System.out.println(mm);
		return Joiner.on("\n").join(invariantPrioritization);
	}

	private void writePrioritization(List<String> prioritization,
			String fileName) {
		String dirName = System.getProperty("prioritization.dir");
		if (dirName != null) {
			File dir = new File(dirName);
			if (dir.exists()) {
				XmlIo.toXML(prioritization, new File(dir, fileName));
			} else {
				throw new RuntimeException("File does not exist " + dir);
			}
		} else {
			throw new RuntimeException("Property not set: prioritization.dir");
		}
	}

	private List<String> prioritize(Multimap<String, Mutation> mm) {
		return AbstractPrioritizer.prioritize(mm, new ScoreCalculator() {

			public int getScore(Collection<Mutation> mutations) {
				int sum = 0;
				for (Mutation mutation : mutations) {
					MutationTestResult result = mutation.getMutationResult();
					if (result != null) {
						sum += result.getDifferentViolatedInvariants();
					}
				}
				return sum;
			}
		});
	}

}
