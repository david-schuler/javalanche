package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestReplaceReturn extends BaseTestReplace {

	private static Class<?> classUnderTest = ReplaceReturnTEMPLATE.class;

	public TestReplaceReturn() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		Method method = clazz.getMethod("m1", int.class);
		assertNotNull(method);
		checkUnmutated(2, 800, method, clazz);
		check(8, 2, 0, 3, method, mutationsForClass, clazz);
	}
}
