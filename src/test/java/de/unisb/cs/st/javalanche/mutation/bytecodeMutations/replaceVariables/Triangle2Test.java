package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariableClass1;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.Triangle2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class Triangle2Test extends BaseBytecodeTest {

	private Class<?> clazz;

	public Triangle2Test() throws Exception {
		super(Triangle2TEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testStaticIntsClass() throws Exception {
		ByteCodeTestUtils.deleteMutations(Triangle2TEMPLATE.class
				.getCanonicalName());
		List<Mutation> mutations = TestUtil
				.getMutationsForClazzOnClasspath(Triangle2TEMPLATE.class);
		List<Mutation> filteredMutations = TestUtil.filterMutations(mutations,
				MutationType.REPLACE_VARIABLE);
		int res = filteredMutations.size();
		Collections.sort(filteredMutations);
		assertEquals(4, TestUtil.filterMutations(filteredMutations, 6).size());
		assertEquals(2, TestUtil.filterMutations(filteredMutations, 7).size());
		assertEquals(3, TestUtil.filterMutations(filteredMutations, 8).size());
		assertEquals(3, TestUtil.filterMutations(filteredMutations, 9).size());
		assertEquals(4, TestUtil.filterMutations(filteredMutations, 12).size());
		assertEquals(58, res);

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
