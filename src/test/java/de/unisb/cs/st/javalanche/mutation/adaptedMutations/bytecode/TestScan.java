package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.properties.TestProperties.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;
public class TestScan {


	@Test
	public void testPossibilitiesReplace() throws Exception {
		String className = ADVICE_CLASS.getClassName();
		byte[] classBytes = ADVICE_CLASS.getClassBytes();
		List<Mutation> mutations = TestUtil.getMutations(classBytes, className);
		int s1 = TestUtil.filterMutations(mutations, MutationType.RIC_ZERO)
				.size();
		int s2 = TestUtil.filterMutations(mutations, MutationType.RIC_PLUS_1)
				.size();
		int s3 = TestUtil.filterMutations(mutations, MutationType.RIC_MINUS_1)
				.size();
		Integer res = s1 + s2 + s3;
		assertThat(res, greaterThan(40));
	}

	@Test
	public void testPossibilitiesJumps() throws Exception {
		String className = ADVICE_CLASS.getClassName();
		byte[] classBytes = ADVICE_CLASS.getClassBytes();
		List<Mutation> mutations = TestUtil.getMutations(classBytes, className);
		int res = TestUtil.filterMutations(mutations, MutationType.NEGATE_JUMP)
				.size();
		assertThat(res, greaterThan(40));
	}
}
