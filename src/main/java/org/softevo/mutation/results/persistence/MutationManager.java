package org.softevo.mutation.results.persistence;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.results.Mutation;

public class MutationManager {


	private static Logger logger = Logger.getLogger(MutationManager.class);


	public static boolean shouldApplyMutation(Mutation mutation) {
		Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
		if(mutationFromDb == null){
			logger.log(Level.INFO, "mutation not in db" + mutation);
			//QueryManager.saveMutation(mutation);
			return false;
		}
		return true;
	}

}
