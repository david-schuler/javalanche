package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestReplaceField extends BaseTestReplace {

	private static Class<?> classUnderTest = ReplaceFieldTEMPLATE.class;

	public TestReplaceField() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		Method method = clazz.getMethod("m1", int.class);
		assertNotNull(method);
		checkUnmutated(10, 50, method, clazz);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
		check(7, 10, 0, 1, method, mutationsForClass, clazz,
				MutationType.ADAPTED_REPLACE_FIELD);
		check(5, 10, 10, 1, method, mutationsForClass, clazz,
				MutationType.ADAPTED_REPLACE_FIELD);
	}
}
