package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverage;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class DeleteTest<T> {

	private static final String TEST_PREFIX = "test.test";
	private Mutation m;
	
	@Test
	public void testDeleteResult() {
		 setUp();
		List<TestMessage> passing = Arrays.asList(new TestMessage(
				"test.test.a", "pass", 3l));
		List<TestMessage> failing = Arrays.asList(new TestMessage(
				"test.test.b", "fail", 3l));
		List<TestMessage> errors = Arrays.asList(new TestMessage("test.test.c",
				"error", 3l));
		MutationTestResult mutationTestResult = new MutationTestResult(passing,
				failing, errors, true);
		m.setMutationResult(mutationTestResult);
		QueryManager.saveMutation(m);
		System.out.println(mutationTestResult.getId());
		assertNotNull(mutationTestResult);
		assertTrue(mutationTestResult.getId() != 0);
		assertTrue(passing.get(0).getId() != 0);
		assertTrue(failing.get(0).getId() != 0);
		assertTrue(errors.get(0).getId() != 0);
		String backup = MutationProperties.PROJECT_PREFIX;
		MutationProperties.PROJECT_PREFIX = TEST_PREFIX;
		ResultDeleter.deleteAllWithPrefix();
		MutationDeleter.deleteAllWithPrefix();
		MutationProperties.PROJECT_PREFIX = backup;
		expectDelete(mutationTestResult.getId(), MutationTestResult.class);
		expectDelete(passing.get(0).getId(), TestMessage.class);
		expectDelete(failing.get(0).getId(), TestMessage.class);
		expectDelete(errors.get(0).getId(), TestMessage.class);
	}

	@Before
	public void setUp() {
		m = new Mutation(TEST_PREFIX + " .Test", 1, 2,
				MutationType.ARITHMETIC_REPLACE, false);
		Mutation dbMutation = QueryManager.getMutationOrNull(m);
		if (dbMutation != null) {
			QueryManager.delete(dbMutation);
		}
	}

	private <T> void expectDelete(Long id, Class<T> clazz) {
		T objectById = QueryManager.getObjectById(id, clazz);
		assertNull("Expected Object to be deleted ", objectById);
	}

	@Test
	public void testDeleteMutation() {
		setUp();
		MutationTestResult mutationTestResult = new MutationTestResult();
		m.setMutationResult(mutationTestResult);
		QueryManager.saveMutation(m);
		System.out.println(mutationTestResult.getId());
		assertTrue(mutationTestResult.getId() != 0);
		String backup = MutationProperties.PROJECT_PREFIX;
		MutationProperties.PROJECT_PREFIX = TEST_PREFIX;
		MutationDeleter.deleteAllWithPrefix();
		MutationProperties.PROJECT_PREFIX = backup;
		MutationTestResult objectById = QueryManager.getObjectById(
				mutationTestResult.getId(), MutationTestResult.class);
		assertNull("Expected Object to be deleted ", objectById);
	}

	@Test
	public void testDeleteCoverageMutation() {
		 setUp();
		MutationTestResult mutationTestResult = new MutationTestResult();
		m.setMutationResult(mutationTestResult);
		QueryManager.saveMutation(m);
		TestName testName = new TestName(TEST_PREFIX + "Testa", TEST_PREFIX, 1l);
		List<TestName> asList = Arrays.asList(testName);

		
		QueryManager.save(testName);
		
		assertTrue(mutationTestResult.getId() != 0);
		assertTrue(testName.getId() != 0);


		String backup = MutationProperties.PROJECT_PREFIX;
		MutationProperties.PROJECT_PREFIX = TEST_PREFIX;
		MutationDeleter.deleteAllWithPrefix();
		MutationProperties.PROJECT_PREFIX = backup;
		expectDelete(mutationTestResult.getId(), MutationTestResult.class);
		expectDelete(testName.getId(), TestName.class);
		
	}
}
