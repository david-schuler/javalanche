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
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

/**
 * 
 * Utility class that is used by several analyze classes.
 * 
 * @author David Schuler
 * 
 */
public class AnalyzeUtil {

	/**
	 * Formats a given double value to percent.
	 * 
	 * @param percent
	 * @return a formated percent string.
	 */
	static String formatPercent(double percent) {
		return String.format("%2.2f%%", percent * 100);
	}

	/**
	 * Formats a given the given values to percent string.
	 * 
	 * @param fraction
	 * @param total
	 * @return a formated percent string.
	 */
	public static String formatPercent(int fraction, int total) {
		return formatPercent((double) fraction / total);
	}

	/**
	 * Comparator that compares mutations regarding their total number of
	 * invariant violations
	 */
	public static final Comparator<Mutation> TOTAL_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getTotalViolations()
					- mutationResult2.getTotalViolations();
		}

	};

	/**
	 * Comparator that compares mutations regarding their total number of
	 * invariant violations
	 */
	public static final Comparator<Mutation> DIFFERENT_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getDifferentViolatedInvariants()
					- mutationResult2.getDifferentViolatedInvariants();
		}

	};

	/**
	 * Returns a multimap that contains a mapping between the tests and the
	 * mutations that are detected by the test.
	 * 
	 * @param mutations
	 *            the mutations to check.
	 * @return the multimap between the tests and mutations.
	 */
	public static Multimap<String, Mutation> getDetectedByTest(
			Iterable<Mutation> mutations) {
		Multimap<String, Mutation> mm = HashMultimap.create();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				Collection<TestMessage> errors = mutationResult.getErrors();
				Collection<TestMessage> failures = mutationResult.getFailures();
				Set<TestMessage> all = new HashSet<TestMessage>(errors);
				all.addAll(failures);
				for (TestMessage testMessage : all) {
					String testCaseName = testMessage.getTestCaseName();
					mm.put(testCaseName, mutation);
				}
			}
		}
		return mm;
	}

}
