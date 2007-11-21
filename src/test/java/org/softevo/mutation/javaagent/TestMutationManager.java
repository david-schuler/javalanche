package org.softevo.mutation.javaagent;

import org.junit.Assert;
import org.junit.Test;
import org.softevo.mutation.results.Mutation;

public class TestMutationManager {

	@Test
	public void testGetClassesToMutate() {
		MutationForRun mutationManager = MutationForRun.getInstance();
		for (Mutation mutation : mutationManager.getMutations()) {
			Assert.assertNotNull(mutation);
		}
		for(String className : mutationManager.getClassNames()){
			Assert.assertNotNull(className);
		}
	}
}
