package org.softevo.mutation.testsuite;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.javaagent.MutationManager;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationSwitcher {

	private static Logger logger = Logger.getLogger(MutationSwitcher.class);

	private Collection<Mutation> mutations;

	private Iterator<Mutation> iter;

	private Mutation actualMutation;

	Collection<String> names;

	public MutationSwitcher(Collection<String> name) {
		// initMutations();
		this.names = name;
	}

	private void initMutations() {
		if (mutations == null) {
			// mutations = QueryManager.getAllMutations();
//			mutations = QueryManager.getAllMutationsForTestCases(names);
			mutations = MutationManager.getInstance().getMutations();
			logger.info(mutations);
			iter = mutations.iterator();
		} else {
			throw new RuntimeException("Already initialized");
		}
	}

	public Set<String> getTests() {
		String[] testCases = QueryManager.getTestCases(actualMutation);
		if (testCases == null) {
			return null;
		}
		return new HashSet<String>(Arrays.asList(testCases));
	}

	public boolean hasNext() {
		if (iter == null) {
			initMutations();
		}
		return iter.hasNext();
	}

	public Mutation next() {
		if (iter == null) {
			initMutations();
		}
		while (iter.hasNext()) {
			actualMutation = iter.next();
			if (actualMutation.getMutationResult() == null) {
				return actualMutation;
			} else {
				logger.info("Mutation already got Results");
			}
		}
		return actualMutation;
	}

	public void switchOn() {
		if (actualMutation != null) {
			logger.info("enabling mutation: "
					+ actualMutation.getMutationVariable() + " in line "
					+ actualMutation.getLineNumber() + " - "
					+ actualMutation.toString());
			System.setProperty(actualMutation.getMutationVariable(), "1");
		}
	}

	public void switchOff() {
		if (actualMutation != null) {
			System.clearProperty(actualMutation.getMutationVariable());
			logger.info("disabling mutation: "
					+ actualMutation.getMutationVariable());
		}

	}
}
