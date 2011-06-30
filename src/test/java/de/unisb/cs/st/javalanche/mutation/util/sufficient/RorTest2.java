package de.unisb.cs.st.javalanche.mutation.util.sufficient;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AddOffutt96Sufficient;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.RorTEMPLATE;

public class RorTest2 extends BaseBytecodeTest {

	private Class<?> clazz;

	public RorTest2() throws Exception {
		super(RorTEMPLATE.class);
		clazz = prepareTest();
	}

	@Override
	public Class<?> prepareTest() throws Exception {
		JavalancheConfiguration configBack = ConfigurationLocator
				.getJavalancheConfiguration();

		ConfigurationLocator.setJavalancheConfiguration(config);
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		List<Mutation> pos = scan(classFile);
		System.out.println("SCAN: " + pos.size());
		//
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 6);
		List<Mutation> mutationsLine10 = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 10);
		Long idLine6 = mutations.get(0).getId();
		Long idLine10 = mutationsLine10.get(0).getId();
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		coverageMap.put(idLine6, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine10, new HashSet<String>(Arrays.asList("test1")));
		MutationCoverageFile.saveCoverageData(coverageMap);
		String prefix = config.getProjectPrefix();
		config.setProjectPrefix("de.unisb.cs.st.javalanche.mutation.util.sufficient");
		AddOffutt96Sufficient.addRorMutations();
		config.setProjectPrefix(prefix);
		//

		redefineMutations(className, config);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		ConfigurationLocator.setJavalancheConfiguration(configBack);
		return clazz;
	}

	@Test
	public void testMutationsLine6() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class, int.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 6);
		assertEquals(7, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			// Note: x > y gets translated to PUSH X, PUSH Y, IF_ICMPLE
			// Thus IF_ICMPEQ corresponds to x != y,
			// and IF_ICMPNE corresponds to x == y and so forth.
			if (addInfo == Opcodes.IF_ICMPEQ) {
				Integer[] input = new Integer[] { 1, 2 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == Opcodes.IF_ICMPNE) {
				Integer[] input = new Integer[] { 2, 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);
			}
			if (addInfo == Opcodes.IF_ICMPLT) {
				Integer[] input = new Integer[] { 1, 1 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == Opcodes.IF_ICMPGE) {
				Integer[] input = new Integer[] { 2, 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);
			}
			if (addInfo == Opcodes.IF_ICMPGT) {
				Integer[] input = new Integer[] { 2, 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);
			}
			if (addInfo == NegateJumpsMethodAdapter.POP_TWICE_TRUE) {
				Integer[] input = new Integer[] { 2, 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);
			}
			if (addInfo == NegateJumpsMethodAdapter.POP_TWICE_FALSE) {
				Integer[] input = new Integer[] { 1, 2 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
		}
	}

	@Test
	public void testMutationsLine10() throws Exception {
		Method m1 = clazz.getMethod("m2", int.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 10);
		assertEquals(7, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.IFEQ) {
				Integer[] input = new Integer[] { -1 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == Opcodes.IFNE) {
				Integer[] input = new Integer[] { 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);

			}
			if (addInfo == Opcodes.IFLT) {
				Integer[] input = new Integer[] { 0 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == Opcodes.IFGE) {
				Integer[] input = new Integer[] { -1 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == Opcodes.IFGT) {
				Integer[] input = new Integer[] { -1 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
			if (addInfo == NegateJumpsMethodAdapter.POP_ONCE_TRUE) {
				Integer[] input = new Integer[] { 1 };
				checkUnmutated(input, true, m1, clazz);
				checkMutation(mutation, input, false, m1, clazz);
			}
			if (addInfo == NegateJumpsMethodAdapter.POP_ONCE_FALSE) {
				Integer[] input = new Integer[] { -1 };
				checkUnmutated(input, false, m1, clazz);
				checkMutation(mutation, input, true, m1, clazz);
			}
		}
	}
}
