package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.CollectorByteCodeTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCalls;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCallsTest;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class RemoveMethodCallsTest {

	// -javaagent:./target/javaagent.jar -Dinvariant.mode=OFF
	// -Dmutation.run.mode=mutation-no-invariant
	// -Dmutation.coverage.information=false
	private static class RemoveMethodCallsTransformer extends
			CollectorByteCodeTransformer {

		@Override
		protected ClassVisitor classVisitorFactory(ClassWriter cw) {
			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(
					System.out));
			return new RemoveMethodCallsPossibilityClassAdapter(cw, mpc,
					new HashMap<Integer, Integer>());
		}

	}

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCalls";
		ByteCodeTestUtils
				.doSetup(classname, new RemoveMethodCallsTransformer());
	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = MethodCalls.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = MethodCalls.class
			.getName();

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 4);

	private static final int[] linenumbers = { 7, 19, 31, 44 };

	@Before
	public void setup() {
		// ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void testMakeMavenHappy() {

	}
//	@Test
//	public void runTests() {
//		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
//		TestSuite suite = new TestSuite(MethodCallsTest.class);
//		selectiveTestSuite.addTest(suite);
//		@SuppressWarnings("unused")
//		MethodCalls methodCalls = new MethodCalls();
//		selectiveTestSuite.run(new TestResult());
//		testResults(TEST_CLASS_NAME);
//	}

	/**
	 * Tests if exactly one testMethod failed because of the mutation.
	 *
	 * @param testClassName
	 *            The class that test the mutated class.
	 */
	@SuppressWarnings("unchecked")
	private static void testResults(String testClassName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		int nonNulls = 0;
		for (Mutation m : mList) {
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				if (m.getMutationType() != Mutation.MutationType.NO_MUTATION
						&& m.getLineNumber() != 44) {
					Assert.assertEquals("Mutation: " + m, 1, singleTestResult
							.getNumberOfErrors()
							+ singleTestResult.getNumberOfFailures());
				}
			} else {
				System.out.println(m);
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected failing tests because of mutations",
				nonNulls >= mList.size());
	}

}
