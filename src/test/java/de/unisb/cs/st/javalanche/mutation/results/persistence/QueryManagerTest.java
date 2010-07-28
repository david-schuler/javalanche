package de.unisb.cs.st.javalanche.mutation.results.persistence;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.junit.Assert.*;

public class QueryManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {

		String clazz = "test.C1";
		Mutation m1 = new Mutation(clazz, "m1", 12, 0, ADAPTED_JUMP);
		Mutation m2 = new Mutation(clazz, "m1", 12, 0, ADAPTED_ALWAYS_ELSE);
		Mutation m3 = new Mutation(clazz, "m1", 12, 0,
				ADAPTED_NEGATE_JUMP_IN_IF);
		Mutation m4 = new Mutation(clazz, "m1", 12, 0,
				ADAPTED_REMOVE_CHECK);
		Mutation m5 = new Mutation(clazz, "m1", 12, 0, ADAPTED_SKIP_ELSE);
		Mutation m6 = new Mutation(clazz, "m1", 12, 0, ADAPTED_SKIP_IF);
		Mutation[] mutations = new Mutation[] { m1, m2, m3, m4, m5, m6 };
		QueryManager.deleteMutations(clazz);
		for (Mutation m : mutations) {
			QueryManager.save(m);
		}
		List<Mutation> adaptedJumpMutations = QueryManager
				.getAdaptedJumpMutations(m1);
		assertEquals(5, adaptedJumpMutations.size());
	}
}
