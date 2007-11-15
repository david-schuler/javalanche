package org.softevo.mutation.results.persistence;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.results.Mutation;

/**
 * Decides if a mutation should be applied in bytecode when the class is loaded.
 *
 * @author David Schuler
 *
 */
public class MutationManager {

	/**
	 * If set to true all mutations from the database are applied, otherwise
	 * only the mutations given by the {@link MutationForRun}
	 */
	private static boolean applyAllMutation = false;

	private static Logger logger = Logger.getLogger(MutationManager.class);

	public static boolean shouldApplyMutation(Mutation mutation) {
		if(mutation == null){
			logger.log(Level.INFO, "Null Mutation");
			return false;
		}
		Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
		if (mutationFromDb == null) {
			logger.log(Level.INFO, "Mutation not in db: " + mutation);
			return false;
		} else if (MutationForRun.getInstance()
				.containsMutation(mutationFromDb)) {
			logger.log(Level.INFO, "Applying mutation: " + mutation);
			MutationForRun.mutationApplied(mutationFromDb);
			return true;
		}
		return applyAllMutation;
	}

	/**
	 * If set to true all mutations from the database are applied, otherwise
	 * only the mutations given by the {@link MutationForRun}
	 *
	 * @param applyAllMutation
	 *            the applyAllMutation to set
	 */
	public static void setApplyAllMutation(boolean applyAllMutation) {
		MutationManager.applyAllMutation = applyAllMutation;
	}

}
