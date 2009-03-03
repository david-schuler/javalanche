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
