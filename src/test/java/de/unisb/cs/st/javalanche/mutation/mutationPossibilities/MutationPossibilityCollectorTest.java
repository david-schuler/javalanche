package de.unisb.cs.st.javalanche.mutation.mutationPossibilities;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
public class MutationPossibilityCollectorTest {

	private static final String NAME = MutationPossibilityCollectorTest.class
			.getName();

	private static final String METHOD_NAME = "method1";

	private MutationPossibilityCollector mpc;

	Mutation m1 = new Mutation(NAME, METHOD_NAME, 41, 0,
			MutationType.NEGATE_JUMP);
	Mutation m2 = new Mutation(NAME, METHOD_NAME, 42, 0,
			MutationType.NEGATE_JUMP);
	Mutation m3 = new Mutation(NAME, METHOD_NAME, 43, 0,
			MutationType.NEGATE_JUMP);

	@Before
	public void setUp() {
		mpc = new MutationPossibilityCollector();
	}

	@Test
	public void testAddPosibility() {
		mpc.addPossibility(m1);
		assertThat("Size ", mpc.size(), is(1));
		mpc.addPossibility(m1);
		assertThat("Size ", mpc.size(), is(1));
		mpc.addPossibility(m2);
		assertThat("Size ", mpc.size(), is(2));

	}

	@Test
	public void testClear() {
		mpc.addPossibility(m1);
		mpc.addPossibility(m2);
		assertThat("Size ", mpc.size(), is(2));
		mpc.clear();
		assertThat("Size ", mpc.size(), is(0));
	}

	@Test
	public void testToDb() {
		QueryManager.delete(m1);
		QueryManager.delete(m2);
		mpc.addPossibility(m1);
		assertThat("Size ", mpc.size(), is(1));
		mpc.toDB();
		mpc.addPossibility(m2);
		Mutation dbMutation1 = QueryManager.getMutationOrNull(m1);
		Mutation dbMutation2 = QueryManager.getMutationOrNull(m2);
		assertThat(dbMutation1, notNullValue());
		assertThat(dbMutation2, nullValue());
		mpc.toDB();
		dbMutation1 = QueryManager.getMutationOrNull(m1);
		dbMutation2 = QueryManager.getMutationOrNull(m2);
		assertThat(dbMutation1, notNullValue());
		assertThat(dbMutation2, notNullValue());
		QueryManager.delete(m1);
		QueryManager.delete(m2);
	}

	@Test
	public void updateDb() {
		QueryManager.delete(m1);
		QueryManager.delete(m2);
		mpc.addPossibility(m1);
		assertThat("Size ", mpc.size(), is(1));
		mpc.updateDB();
		mpc.addPossibility(m2);
		Mutation dbMutation1 = QueryManager.getMutationOrNull(m1);
		Mutation dbMutation2 = QueryManager.getMutationOrNull(m2);
		assertThat(dbMutation1, notNullValue());
		assertThat(dbMutation2, nullValue());
		mpc.updateDB();
		dbMutation1 = QueryManager.getMutationOrNull(m1);
		dbMutation2 = QueryManager.getMutationOrNull(m2);
		assertThat(dbMutation1, notNullValue());
		assertThat(dbMutation2, notNullValue());
		QueryManager.delete(m1);
		QueryManager.delete(m2);
	}

	@Test
	public void testGetMutations() {
		mpc.addPossibility(m1);
		mpc.addPossibility(m2);
		List<Mutation> possibilities = mpc.getPossibilities();
		assertThat(possibilities, hasItem(m1));
		assertThat(possibilities, hasItem(m2));
		assertThat(possibilities, not(hasItem(m3)));
	}

	@Test
	public void testToString() {
		mpc.addPossibility(m1);
		mpc.addPossibility(m2);
		String toString = mpc.toString();
		assertThat(toString, containsString(m1.toString()));
		assertThat(toString, containsString(m2.toString()));
	}
}
