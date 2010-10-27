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
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.invariants.runtime.InvariantObserver;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * Class collects the invariants that where violated by every test, and adds
 * them to the mutation's result if they where learned from this test.
 *
 * @author David Schuler
 *
 */
public class InvariantPerTestListener implements MutationTestListener {

	private static Logger logger = Logger
			.getLogger(InvariantPerTestListener.class);

	private Map<String, Set<Integer>> addViolationsPerTest = new HashMap<String, Set<Integer>>();

	private Map<String, Set<Integer>> missedViolationsPerTest = new HashMap<String, Set<Integer>>();

	private final Set<Integer> totalViolations = new HashSet<Integer>();

	public void end() {
	}

	public void mutationEnd(Mutation mutation) {
		MutationTestResult mutationResult = mutation.getMutationResult();
		// InvariantAddResult invariantAddResult = new InvariantAddResult(
		// violationsPerTest);
		// mutationResult.addResults(invariantAddResult);
		mutationResult.setDifferentViolatedInvariants(totalViolations.size());
		mutationResult.setTotalViolations(totalViolations.size());
		writeViolations("add", mutation, addViolationsPerTest);
		writeViolations("missed", mutation, missedViolationsPerTest);
		// mutationResult.setViolatedInvariants(PrimitiveArrays
		// .toIntArray(totalViolations));

	}

	private void writeViolations(String prefix, Mutation mutation,
			Map<String, Set<Integer>> map) {
		int totalSize = 0;
		for (Set<Integer> s : map.values()) {
			totalSize += s.size();
		}
		logger.info(prefix + " invariants " + totalSize);
		SerializeIo.serializeToFile(map, new File("invariant-files/" + prefix
				+ "-" + mutation.getId()));
	}

	public void mutationStart(Mutation mutation) {
		totalViolations.clear();
		addViolationsPerTest.clear();
		missedViolationsPerTest.clear();
		InvariantObserver.reset();
	}

	public void start() {
	}

	public void testEnd(String testName) {
		InvariantObserver instance = InvariantObserver.getInstance();
		if (instance != null) {

			int totalViolatedInvariants = instance
					.getTotalInvariantViolations();
			if (totalViolatedInvariants > 0) {
				Set<Integer> violatedInvariants = instance
						.getViolatedInvariants();
				logger.info("violatedInvariants " + violatedInvariants.size());
				Set<Integer> unMutatedViolations = InvariantUtils
						.getUnmutatedViolations(testName);
				Set<Integer> addViolations = new HashSet<Integer>(
						violatedInvariants);
				addViolations.removeAll(unMutatedViolations);

				Set<Integer> missedViolations = new HashSet<Integer>(
						unMutatedViolations);
				missedViolations.removeAll(violatedInvariants);
				logger.info(addViolations.size()
						+ " additional violations and "
						+ missedViolations.size() + " missed violations");
				addViolationsPerTest.put(testName, addViolations);
				missedViolationsPerTest.put(testName, missedViolations);
			}
		}
	}

	public void testStart(String testName) {
		InvariantObserver.reset();
	}

}
