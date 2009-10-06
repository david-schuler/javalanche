package de.unisb.cs.st.javalanche.mutation.javaagent;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationForRunTest {

	private static Mutation m1 = new Mutation(MutationForRunTest.class
			.getName(), 41, 0, MutationType.NEGATE_JUMP, false);

	private static Mutation m2 = new Mutation(MutationForRunTest.class
			.getName(), 42, 0, MutationType.NEGATE_JUMP, false);

	private static Mutation m3 = new Mutation(MutationForRunTest.class
			.getName(), 43, 0, MutationType.NEGATE_JUMP, false);

	private static MutationForRun mfr;

	private static File f;

	@BeforeClass
	public static void setupClass() throws IOException {
		QueryManager.save(m1);
		QueryManager.save(m3);
		QueryManager.save(m2);
		f = File.createTempFile("test", "test");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write(m1.getId() + "\n");
		w.write(m2.getId() + "\n");
		w.flush();
		w.close();
		mfr = new MutationForRun(f.getAbsolutePath());

	}

	@AfterClass
	public static void tearDownClass() {
		QueryManager.deleteMutations(MutationForRunTest.class.getName());
	}

	@Test
	public void testContains() {
		assertTrue(mfr.containsMutation(m1));
		assertTrue(mfr.containsMutation(m2));
		assertFalse(mfr.containsMutation(m3));
	}

	@Test
	public void testGetMutations() {
		List<Mutation> mutations = mfr.getMutations();
		assertThat(mutations, hasItems(m1, m2));
		assertThat(mutations, not(hasItem(m3)));
		assertThat(mutations.size(), is(2));
	}

	@Test
	public void testGetClassNames() {
		Collection<String> classNames = mfr.getClassNames();
		assertThat(classNames.size(), is(1));
		assertThat(classNames, hasItem(MutationForRunTest.class.getName()));
	}

	@Test
	public void testGetFromDefaultLocation() {
		MutationProperties.MUTATION_FILE_NAME = f.getAbsolutePath();
		MutationForRun fromDefaultLocation = MutationForRun
				.getFromDefaultLocation();
		assertThat(fromDefaultLocation.getMutations(), is(mfr.getMutations()));
	}
}