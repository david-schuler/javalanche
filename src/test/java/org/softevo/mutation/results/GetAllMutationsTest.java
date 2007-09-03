package org.softevo.mutation.results;

import java.util.List;

import org.junit.Test;
import org.softevo.mutation.results.persistence.QueryManager;

public class GetAllMutationsTest {

	@Test
	public void testgetAllMutations() {
		List<Mutation> l = QueryManager.getAllMutations();
		for (Mutation m : l) {
			System.out.println(m);
		}
	}
}
