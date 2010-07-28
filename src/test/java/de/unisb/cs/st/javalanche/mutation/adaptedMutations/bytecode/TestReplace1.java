package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestReplace1 extends BaseTestReplace {

	private static Class<?> classUnderTest = ReplaceTEMPLATE.class;

	public TestReplace1() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		Method method = clazz.getMethod("m1", int.class);
		assertNotNull(method);
		check(9, 0, 2, method, mutationsForClass, clazz);
		method = clazz.getMethod("m2", int.class);
		assertNotNull(method);
		check(13, 0, 2, method, mutationsForClass, clazz);
		method = clazz.getMethod("m3", int.class);
		assertNotNull(method);
		check(18, 0, 2, method, mutationsForClass, clazz);
	}

	private void check(int lineNumber, int expectedOutput,
			int expectedMutations, Method method,
			List<Mutation> mutationsForClass, Class<?> clazz) throws Exception {
		check(lineNumber, 2, expectedOutput, expectedMutations, method,
				mutationsForClass, clazz);
	}
}
