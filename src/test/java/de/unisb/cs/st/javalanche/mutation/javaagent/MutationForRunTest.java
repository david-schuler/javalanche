package de.unisb.cs.st.javalanche.mutation.javaagent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class MutationForRunTest {

	private static Mutation m1 = new Mutation(
			MutationForRunTest.class.getName(), "testMethod", 41, 0,
			MutationType.NEGATE_JUMP);

	private static Mutation m2 = new Mutation(
			MutationForRunTest.class.getName(), "testMethod", 42, 0,
			MutationType.NEGATE_JUMP);

	private static Mutation m3 = new Mutation(
			MutationForRunTest.class.getName(), "testMethod", 43, 0,
			MutationType.NEGATE_JUMP);

	private static Mutation mutationWithResult = new Mutation(
			MutationForRunTest.class.getName(), "testMethod", 55, 0,
			MutationType.NEGATE_JUMP);

	private static MutationsForRun mfr;

	private static File tempFile;

	private JavalancheConfiguration back;

	private JavalancheTestConfiguration cfg;

	private static File testDir;

	@BeforeClass
	public static void setupClass() throws IOException {
		testDir = new File("target/tmp");
		testDir.mkdirs();
		QueryManager.save(m1);
		QueryManager.save(m3);
		QueryManager.save(m2);
		MutationTestResult mutationTestResult = new MutationTestResult();
		mutationWithResult.setMutationResult(mutationTestResult);
		QueryManager.save(mutationWithResult);
		tempFile = File.createTempFile("test", "test");
		BufferedWriter w = new BufferedWriter(new FileWriter(tempFile));
		w.write(m1.getId() + "\n");
		w.write(m2.getId() + "\n");
		w.flush();
		w.close();
		mfr = new MutationsForRun(tempFile, true);
	}

	@AfterClass
	public static void tearDownClass() {
		QueryManager.deleteMutations(MutationForRunTest.class.getName());
	}

	@Before
	public void setup() {
		back = ConfigurationLocator.getJavalancheConfiguration();
		cfg = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(cfg);
	}

	@After
	public void tearDown() {
		ConfigurationLocator.setJavalancheConfiguration(back);
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
		JavalancheConfiguration back = ConfigurationLocator
				.getJavalancheConfiguration();
		JavalancheTestConfiguration cfg = new JavalancheTestConfiguration();
		cfg.setMutationIdFile(tempFile);
		ConfigurationLocator.setJavalancheConfiguration(cfg);

		MutationsForRun fromDefaultLocation = MutationsForRun
				.getFromDefaultLocation();
		assertThat(fromDefaultLocation.getMutations(), is(mfr.getMutations()));

		ConfigurationLocator.setJavalancheConfiguration(back);
	}

	@Test
	public void testFilterWithResult() throws IOException {
		File f2 = File.createTempFile("test2", "test2");
		BufferedWriter w = new BufferedWriter(new FileWriter(f2));
		w.write(m1.getId() + "\n");
		w.write(mutationWithResult.getId() + "\n");
		w.flush();
		w.close();
		MutationsForRun mfr2 = new MutationsForRun(f2, true);
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
		MutationsForRun mfr2 = new MutationsForRun(f2, false);
		List<Mutation> mutations = mfr2.getMutations();
		assertEquals("Expected mutation with result not to be filtered", 2,
				mutations.size());
	}

	@Test
	public void testEmptyFile() throws IOException {
		File fEmpty = File.createTempFile("testEmpty", "testEmpty");
		fEmpty.deleteOnExit();
		assertTrue(fEmpty.exists());
		MutationsForRun mutationsForRun = new MutationsForRun(fEmpty, true);
		List<Mutation> mutations = mutationsForRun.getMutations();
		assertThat(mutations.size(), is(0));
	}

	@Test
	public void testFileDoesNotExist() throws IOException {
		String name = "non_exisiting_file";
		try {
			new MutationsForRun(new File(name), true);
			fail("Expected exception");
		} catch (RuntimeException e) {
			assertThat(
					e.getMessage(),
					containsString("Given id file does not exist:"));
			assertThat(e.getMessage(), containsString(name));
		}
	}

	@Test
	public void testNull() throws IOException {
		try {
			MutationsForRun mutationsForRun = new MutationsForRun(null, true);
			fail("Expected exception");
		} catch (RuntimeException e) {
			assertThat(
					e.getMessage(),
					containsString("Given id file does not exist:"));
			assertThat(e.getMessage(), containsString("null"));
		}
	
	}

	@Test
	public void testConfigFileLocation() throws IOException {
		File f = new File(testDir, "mutation-task-org_test-01.txt");
		cfg.setMutationIdFile(f);
		cfg.setProjectPrefix("org.test");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write(m1.getId() + "\n");
		w.write(mutationWithResult.getId() + "\n");
		w.flush();
		w.close();
		MutationsForRun mfr2 = MutationsForRun.getFromDefaultLocation();
		f.delete();
		List<Mutation> mutations = mfr2.getMutations();
		assertEquals("Expected mutation with result not to be filtered", 1,
				mutations.size());
	}

	@Test
	public void testSingleTaskNonExistingFile() throws IOException {
		String fileName = "mutation-task-org_test2-01.txt";
		File f2 = new File(testDir, fileName);
		cfg.setMutationIdFile(f2);
		cfg.setProjectPrefix("org.test");
		try {
			MutationsForRun.getFromDefaultLocation();
			fail("Expected exception");
		} catch (RuntimeException e) {
			assertThat(e.getMessage(),
					containsString("Given id file does not exist:"));
			assertThat(e.getMessage(), containsString(fileName));
		}
	}
}
