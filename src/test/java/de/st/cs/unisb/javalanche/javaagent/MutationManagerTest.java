package de.st.cs.unisb.javalanche.javaagent;

import org.junit.Assert;
import org.junit.Test;
import de.st.cs.unisb.javalanche.results.Mutation;

public class MutationManagerTest {

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
