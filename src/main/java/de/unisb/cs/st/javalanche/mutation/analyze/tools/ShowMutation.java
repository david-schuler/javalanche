package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Displays all unit tests that cover a specified mutation.
 *
 * @author David Schuler
 *
 */
public class ShowMutation {

	public static void main(String[] args) {
		if (args.length > 0) {
			Long l = Long.parseLong(args[0]);
			Mutation mutation = QueryManager.getMutationByID(l);
			System.out.println(mutation);
			Set<String> testsCollectedData = QueryManager
					.getTestsCollectedData(mutation);
			for (String string : testsCollectedData) {
				System.out.println(string);
			}
		}
	}

}
