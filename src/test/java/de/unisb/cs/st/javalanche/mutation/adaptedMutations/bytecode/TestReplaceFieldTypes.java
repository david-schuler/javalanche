package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestReplaceFieldTypes extends BaseTestReplace {

	private static Class<?> classUnderTest = ReplaceFieldTypesTEMPLATE.class;

	public TestReplaceFieldTypes() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
		check("m1", 5, 10, 10, 0, clazz, mutationsForClass);
		check("m2", 6, 10, 20, 0, clazz, mutationsForClass);
		check("m3", 7, 10, 30, 0, clazz, mutationsForClass);
		check("m4", 8, 10, 40, 0, clazz, mutationsForClass);
		check("m5", 9, 10, 50, 0, clazz, mutationsForClass);
		check("m6", 10, 10, 60, 0, clazz, mutationsForClass);
		check("m7", 11, 10, 70, 0, clazz, mutationsForClass);
		check("mb", 12, 10, 10, 0, clazz, mutationsForClass);
		check("mo", 13, 10, 10, 0, clazz, mutationsForClass);
	}

	private void check(String methodName, int line, int input,
			int expectedOutput, int expectedMutatedOut, Class<?> clazz,
			List<Mutation> mutationsForClass) throws NoSuchMethodException,
			Exception {
		Method method = clazz.getMethod(methodName, int.class);
		assertNotNull(method);
		checkUnmutated(input, expectedOutput, method, clazz);
		check(line, input, expectedMutatedOut, 1, method, mutationsForClass,
				clazz, MutationType.ADAPTED_REPLACE_FIELD);
	}
}
