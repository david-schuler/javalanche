package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestReplaceField2 extends BaseTestReplace {

	private static Class<?> classUnderTest = ReplaceField2TEMPLATE.class;

	public TestReplaceField2() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		Method method = clazz.getMethod("m1", int.class);
		assertNotNull(method);
		checkUnmutated(10, 40, method, clazz);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
		check(5, 10, 0, 1, method, mutationsForClass, clazz,
				MutationType.ADAPTED_REPLACE_FIELD);
	}
}
