package de.unisb.cs.st.javalanche.mutation.javaagent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationForRunTest {

	private static Mutation m1 = new Mutation(MutationForRunTest.class
			.getName(), "testMethod", 41, 0, MutationType.NEGATE_JUMP);

	private static Mutation m2 = new Mutation(MutationForRunTest.class
			.getName(), "testMethod", 42, 0, MutationType.NEGATE_JUMP);

	private static Mutation m3 = new Mutation(MutationForRunTest.class
			.getName(), "testMethod", 43, 0, MutationType.NEGATE_JUMP);

	private static Mutation mutationWithResult = new Mutation(
			MutationForRunTest.class.getName(), "testMethod", 55, 0,
			MutationType.NEGATE_JUMP);

	private static MutationsForRun mfr;

	private static File f;

	@BeforeClass
	public static void setupClass() throws IOException {
		QueryManager.save(m1);
		QueryManager.save(m3);
		QueryManager.save(m2);
		MutationTestResult mutationTestResult = new MutationTestResult();
		mutationWithResult.setMutationResult(mutationTestResult);
		QueryManager.save(mutationWithResult);
		f = File.createTempFile("test", "test");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write(m1.getId() + "\n");
		w.write(m2.getId() + "\n");
		w.flush();
		w.close();
		mfr = new MutationsForRun(f.getAbsolutePath(), true);
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
		MutationsForRun fromDefaultLocation = MutationsForRun
				.getFromDefaultLocation();
		assertThat(fromDefaultLocation.getMutations(), is(mfr.getMutations()));
	}

	@Test
	public void testFilterWithResult() throws IOException {
		File f2 = File.createTempFile("test2", "test2");
		BufferedWriter w = new BufferedWriter(new FileWriter(f2));
		w.write(m1.getId() + "\n");
		w.write(mutationWithResult.getId() + "\n");
		w.flush();
		w.close();
		MutationsForRun mfr2 = new MutationsForRun(f2.getAbsolutePath(), true);
		List<Mutation> mutations = mfr2.getMutations();
		assertEquals("Expected mutation with result to be filtered", 1,
				mutations.size());
	}

	@Test
	public void testFilterWithResultDisable() throws IOException {
		File f2 = File.createTempFile("test2", "test2");
		BufferedWriter w = new BufferedWriter(new FileWriter(f2));
		w.write(m1.getId() + "\n");
		w.write(mutationWithResult.getId() + "\n");
		w.flush();
		w.close();
		MutationsForRun mfr2 = new MutationsForRun(f2.getAbsolutePath(), false);
		List<Mutation> mutations = mfr2.getMutations();
		assertEquals("Expected mutation with result not to be filtered", 2,
				mutations.size());
	}

	@Test
	public void testEmptyFile() throws IOException {
		File fEmpty = File.createTempFile("testEmpty", "testEmpty");
		fEmpty.deleteOnExit();
		assertTrue(fEmpty.exists());
		MutationsForRun mutationsForRun = new MutationsForRun(fEmpty
				.getAbsolutePath(), true);
		List<Mutation> mutations = mutationsForRun.getMutations();
		assertThat(mutations.size(), is(0));
	}

	@Test
	public void testFileDoesNotExist() throws IOException {
		MutationsForRun mutationsForRun = new MutationsForRun(
				"non_exisiting_file", true);
		List<Mutation> mutations = mutationsForRun.getMutations();
		assertThat(mutations.size(), is(0));
	}

	@Test
	public void testNull() throws IOException {
		MutationsForRun mutationsForRun = new MutationsForRun(null, true);
		List<Mutation> mutations = mutationsForRun.getMutations();
		assertThat(mutations.size(), is(0));
	}

	@Test
	public void testSingleTask() throws IOException {
		MutationProperties.SINGLE_TASK_MODE = true;
		MutationProperties.PROJECT_PREFIX = "org.test";
		File f = new File("mutation-files/mutation-task-org_test-01.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write(m1.getId() + "\n");
		w.write(mutationWithResult.getId() + "\n");
		w.flush();
		w.close();
		MutationsForRun mfr2 = MutationsForRun.getFromDefaultLocation();
		MutationProperties.PROJECT_PREFIX = null;
		MutationProperties.SINGLE_TASK_MODE = false;
		f.delete();
		List<Mutation> mutations = mfr2.getMutations();
		assertEquals("Expected mutation with result not to be filtered", 1,
				mutations.size());
	}

	@Test
	public void testSingleTaskNonExistinFile() throws IOException {
		MutationProperties.SINGLE_TASK_MODE = true;
		MutationProperties.PROJECT_PREFIX = "org.test2";
		File f2 = new File("mutation-files/mutation-task-org_test2-01.txt");
		try {
			MutationsForRun mfr2 = MutationsForRun.getFromDefaultLocation();
			fail("Expected exception");
		} catch (RuntimeException e) {
			// ok
		}
		MutationProperties.PROJECT_PREFIX = null;
		MutationProperties.SINGLE_TASK_MODE = false;
	}
}
