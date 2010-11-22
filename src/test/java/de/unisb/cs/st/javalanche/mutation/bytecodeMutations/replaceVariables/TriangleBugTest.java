package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.TriangleTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class TriangleBugTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public TriangleBugTest() throws Exception {
		super(TriangleTEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void test() throws Exception {
		Method m1 = clazz.getMethod("exe", int.class, int.class, int.class);
		checkUnmutated(new Object[] { 1, 2, 3 }, 1, m1, clazz);
		// checkMutation(12, MutationType.REPLACE_VARIABLE, 0, new Object[0], 2,
		// m1, clazz);
		// Mutation m = new Mutation(className, "exe", 6, 0,
		// MutationType.REPLACE_VARIABLE);
	}

}
