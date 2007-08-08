package org.softevo.mutation.results.persistence;

import org.softevo.mutation.results.Mutation;

public class MutationManager {

	public static boolean shouldApplyMutation(Mutation mutation) {
		Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
		if(mutationFromDb == null){
			QueryManager.saveMutation(mutation);
		}
		return true;
	}

}
