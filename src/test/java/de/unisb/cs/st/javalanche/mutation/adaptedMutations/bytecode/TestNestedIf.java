package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestNestedIf extends BaseTestJump {
	private static Class<?> classUnderTest = NestedIfTEMPLATE.class;

	public TestNestedIf() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		System.out.println("MUTATIONS: " + mutationsForClass);
		Method method = clazz.getMethod("m", int.class);
		assertNotNull(method);
		checkUnmutated(12, 2, method, clazz);
		// check(7, 12, 4, 1, method, mutationsForClass, clazz,
		// MutationType.ADAPTED_ALWAYS_ELSE);
		check(9, 12, 3, 1, method, mutationsForClass, clazz,
				MutationType.ADAPTED_ALWAYS_ELSE);
	}
}