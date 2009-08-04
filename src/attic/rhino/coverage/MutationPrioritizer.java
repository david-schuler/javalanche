package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Join;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationPrioritizationAnalyzer;

public class MutationPrioritizer {

	public static void main(String[] args) {
		getMutationPrioritization();
	}

	public static void getMutationPrioritization() {
		FailureMatrix fm = FailureMatrix.parseFile(new File(
				"/scratch/schuler/subjects/ibugs_rhino-0.1/failureMatrix.csv"));
		File file = MutationPrioritizationAnalyzer.TOTAL_MUTATION_FILE;
		getMutationPrioritization(fm, file);
		file = MutationPrioritizationAnalyzer.ADD_MUTATION_FILE;
		getMutationPrioritization(fm, file);
		file = MutationPrioritizationAnalyzer.TOTAL_MUTATION_INV_FILE;
		getMutationPrioritization(fm, file);
		file = MutationPrioritizationAnalyzer.ADD_MUTATION_INV_FILE;
		getMutationPrioritization(fm, file);
	}

	public static void getMutationPrioritization(FailureMatrix fm, File file) {
		List<String> lines = Io.getLinesFromFile(file);
		List<String> tests = new ArrayList<String>();
		List<Integer> failures = new ArrayList<Integer>();
		List<Integer> x = new ArrayList<Integer>();

		for (String line : lines) {
			String[] split = line.split(",");
			String testAdd = split[0];
			String prefix = "/scratch2/schuler/subjects/ibugs_rhino-0.1/versionsFailureMatrix/277935/post-fix/mozilla/js/tests/";
			if (testAdd.startsWith(prefix)) {
				testAdd = testAdd.substring(prefix.length());
			}
			tests.add(testAdd);
			int detectedFailures = fm.getDetectedFailures(tests);
			failures.add(detectedFailures);
			x.add(tests.size());
		}
		String xJoin = Join.join(",", x);
		String failuresJoin = Join.join(",", failures);

		System.out.println("x <- c(" + xJoin + ")");
		System.out.println("failures <- c(" + failuresJoin + ")");
	}
}
