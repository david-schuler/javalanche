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
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;
import de.unisb.cs.st.javalanche.invariants.runtime.InvariantObserver;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * Class collects the invariants that where violated by every test when no mutation is executed.
 *
 * @author David Schuler
 *
 */
public class InvariantPerTestCheckListener implements MutationTestListener {


	private static Logger logger = Logger
			.getLogger(InvariantPerTestCheckListener.class);

	public void end() {
	}

	public void mutationEnd(Mutation mutation) {
	}


	public void mutationStart(Mutation mutation) {
	}

	public void start() {
	}

	public void testEnd(String testName) {
		InvariantObserver instance = InvariantObserver.getInstance();
		Set<Integer> violations = instance.getViolatedInvariants();
		Set<Integer> idsForThisTest = InvariantUtils.getTestInvariantMap().get(
				testName);
		InvariantObserver.reset(); // do not report violations of last run
		serializeViolations(testName, violations);
		serializeChecked(idsForThisTest, violations, testName);
	}

	private void serializeViolations(String testName, Set<Integer> violations) {
		File violationsFile = InvariantUtils.getViolationsFile(testName);
		if(violationsFile.exists()){
			Set<Integer> unmutatedViolations = new HashSet<Integer>(InvariantUtils.getUnmutatedViolations(testName));
			if(unmutatedViolations.size() != violations.size()){
				logger.warn("Sizes of violation differs");
				throw new RuntimeException("Sizes of violation differs for several runs \n" + unmutatedViolations + "\n" + violations );
			}
			unmutatedViolations.removeAll(violations);
			if(unmutatedViolations.size() == 0){
				logger.warn("Sizes of violation differs");
				throw new RuntimeException("Violated invariants differ. Differing violations " + unmutatedViolations );
			}
		}
		SerializeIo.serializeToFile(violations, violationsFile);
	}

	private void serializeChecked(Set<Integer> idsForThisTest,
			Set<Integer> violations, String testName) {
		if (idsForThisTest != null) {
			File file = InvariantUtils.getInvariantFile(testName);
			File checkedFile = new File(file.getParentFile(),
					InvariantProperties.CHECKED_PREFIX + file.getName());
			idsForThisTest.removeAll(violations);
			SerializeIo.serializeToFile(idsForThisTest, checkedFile);
		}
	}

	public void testStart(String testName) {
		InvariantObserver.reset();
	}

}
