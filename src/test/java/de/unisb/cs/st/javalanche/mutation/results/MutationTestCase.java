package de.unisb.cs.st.javalanche.mutation.results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.AfterClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.junit.Assert.*;

public class MutationTestCase {

	private static final String TEST_METHOD = "TestMethod";
	private static final String TEST_CLASS = "TestClass";

	@AfterClass
	public static void tearDownClass() {
		QueryManager.deleteMutations(TEST_CLASS);
	}

	@Test
	public void testSimpleConstructor() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 1, 0,
				MutationType.ARITHMETIC_REPLACE);
		assertThat(m.getClassName(), is(TEST_CLASS));
		assertThat(m.getMethodName(), is(TEST_METHOD));
		assertThat(m.getLineNumber(), is(1));
		assertThat(m.getMutationForLine(), is(0));
	}

	@Test
	public void testEquals() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 1, 0,
				MutationType.ARITHMETIC_REPLACE);

		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 1, 0,
				MutationType.ARITHMETIC_REPLACE);
		assertThat(m, is(m2));
	}

	@Test
	public void testIllegalArgument1() {
		try {
			Mutation m = new Mutation(null, TEST_METHOD, 1, 0,
					MutationType.ARITHMETIC_REPLACE);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// ok
		}

	}

	@Test
	public void testIllegalArgument2() {
		try {
			Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 1, 0, null);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// ok
		}

	}

	@Test
	public void testIllegalArgument3() {
		try {
			Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, -2, 0,
					MutationType.ARITHMETIC_REPLACE);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// ok
		}

	}

	@Test
	public void testIllegalArgument4() {
		try {
			Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, -1,
					MutationType.ARITHMETIC_REPLACE);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// ok
			System.out.println(e);

		}
	}

	@Test
	public void testIllegalArgument5() {
		try {
			Mutation m = new Mutation(TEST_CLASS, null, 0, 1,
					MutationType.ARITHMETIC_REPLACE);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// ok
		}
	}

	@Test
	public void testTranformClassname() {
		String classNameSlash = "de/test/Clazz";
		String classNameDot = "de.test.Clazz";
		Mutation m = new Mutation(classNameSlash, "m", 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		assertEquals(classNameDot, m.getClassName());
	}

	@Test
	public void testIdSimple() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		m.setId(34l);
		assertEquals(Long.valueOf(34l), m.getId());
	}

	@Test
	public void testIdComplex() {
		Mutation m1 = new Mutation(TEST_CLASS, TEST_METHOD, 1, 1,
				MutationType.ARITHMETIC_REPLACE);

		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 1, 2,
				MutationType.ARITHMETIC_REPLACE);
		QueryManager.saveMutation(m1);
		QueryManager.saveMutation(m2);
		assertThat(m2.getId(), not(is(m1.getId())));
		QueryManager.delete(m1);
		QueryManager.delete(m2);
	}

	@Test
	public void testHashCodeEquals() {
		Mutation m1 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		assertThat(m1, is(m2));
		assertThat(m1.hashCode(), is(m2.hashCode()));
	}

	@Test
	public void testHashCodeNotEquals() {
		Mutation m1 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 1, 2,
				MutationType.ARITHMETIC_REPLACE);
		assertThat(m1, not(is(m2)));
		assertThat(m1.hashCode(), not(is(m2.hashCode())));
	}

	@Test
	public void testIsKilled() {
		Mutation m1 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		assertFalse(m1.isKilled());
		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		TestMessage pass = new TestMessage("t1", "a", 2);
		TestMessage fail = new TestMessage("t2", "a", 2);
		TestMessage error = new TestMessage("t3", "a", 2);
		List<TestMessage> emptyList = new ArrayList<TestMessage>();
		MutationTestResult r = new MutationTestResult(Arrays.asList(pass),
				emptyList, emptyList, true);
		m1.setMutationResult(r);
		assertFalse(m1.isKilled());

		MutationTestResult r2 = new MutationTestResult(Arrays.asList(pass),
				Arrays.asList(fail), emptyList, true);
		m1.setMutationResult(r2);
		assertTrue(m1.isKilled());

		MutationTestResult r3 = new MutationTestResult(Arrays.asList(pass),
				emptyList, Arrays.asList(error), true);
		m1.setMutationResult(r3);
		assertTrue(m1.isKilled());

	}

	@Test
	public void testGetSet() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		m.setClassName("T2");
		assertEquals("T2", m.getClassName());
		m.setMethodName("m2");
		assertEquals("m2", m.getMethodName());
		m.setLineNumber(2);
		assertEquals(2, m.getLineNumber());
		m.setMutationForLine(3);
		assertEquals(3, m.getMutationForLine());
		m.setMutationType(MutationType.NEGATE_JUMP);
		assertEquals(MutationType.NEGATE_JUMP, m.getMutationType());
		m.getMutationType().getDesc();
		m.setAddInfo("Add INFO");
		assertEquals("Add INFO", m.getAddInfo());

	}

	@Test
	public void testToString() {

		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		String string = m.toString();
		assertThat(string, containsString(TEST_CLASS));
		assertThat(string, containsString(TEST_METHOD));
		assertThat(string, containsString("0"));
		assertThat(string, containsString("1"));
		assertThat(string, containsString("No Result"));
	}

	@Test
	public void testToShortString() {

		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		String string = m.toShortString();
		assertThat(string, containsString(TEST_CLASS));
		assertThat(string, containsString(TEST_METHOD));
		assertThat(string, containsString("0"));
		assertThat(string, containsString("1"));
		assertThat(string, containsString("[No Result]"));

	}

	@Test
	public void testMutationVariable() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		m.setId(13l);
		String mutationVariable = m.getMutationVariable();
		assertThat(mutationVariable, containsString("13"));
	}

	@Test
	public void testMutationEqualsWithoutId() {
		Mutation m1 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		Mutation m2 = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		m2.setMutationResult(new MutationTestResult());
		assertTrue(m1.equalsWithoutIdAndResult(m2));
		assertFalse(m1.equals(m2));
		m1.setMutationResult(m2.getMutationResult());
		assertTrue(m1.equalsWithoutIdAndResult(m2));
		assertTrue(m1.equals(m2));
		m1.setId(1l);
		m2.setId(2l);
		assertTrue(m1.equalsWithoutIdAndResult(m2));
		assertFalse(m1.equals(m2));
	}

	@Test
	public void testCsvString() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		String csvString = m.getCsvString();
		assertThat(csvString, containsString(TEST_CLASS));
		assertThat(csvString, containsString(TEST_METHOD));
		assertThat(csvString, containsString("0"));
		assertThat(csvString, containsString("1"));
		assertThat(csvString, containsString(","));
	}

	@Test
	public void testLoadAll() {
		saveMutaiton();
		Mutation queryM = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Mutation loadedMutation = QueryManager.getMutationOrNull(queryM,
				session);
		session.close();
		try {
			loadedMutation.getMutationResult().getAllTestMessages();
			fail("Expected exception");
		} catch (LazyInitializationException e) {
			// expect this
		}
		Session session2 = sessionFactory.openSession();
		Mutation loadedMutation2 = QueryManager.getMutationOrNull(queryM,
				session2);
		loadedMutation2.loadAll();
		session2.close();
		int size = loadedMutation2.getMutationResult().getAllTestMessages()
				.size();
		assertEquals(3, size);
	}

	private Mutation saveMutaiton() {
		Mutation m = new Mutation(TEST_CLASS, TEST_METHOD, 0, 1,
				MutationType.ARITHMETIC_REPLACE);
		TestMessage pass = new TestMessage("t1", "a", 2);
		TestMessage fail = new TestMessage("t2", "a", 2);
		TestMessage error = new TestMessage("t3", "a", 2);
		MutationTestResult r = new MutationTestResult(Arrays.asList(pass),
				Arrays.asList(fail), Arrays.asList(error), true);
		m.setMutationResult(r);
		QueryManager.saveMutation(m);
		return m;
	}

}
