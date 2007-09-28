package org.softevo.mutation.results;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.softevo.mutation.results.persistence.QueryManager;

public class GetAllMutationsTest {

	@Test
	public void testgetAllMutations() {
		List<Mutation> l = QueryManager.getAllMutations();
		Assert.assertTrue(l.size() >10);
	}
}
