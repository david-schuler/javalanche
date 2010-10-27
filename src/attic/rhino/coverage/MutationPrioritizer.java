/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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
