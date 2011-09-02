package de.unisb.cs.st.javalanche.mutation.util.sufficient;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.AbstractArithmeticMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AddOffutt96Sufficient;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.AorTEMPLATE;

public class AorTest extends BaseBytecodeTest {

	protected Class<?> clazz;

	public AorTest() throws Exception {
		super(AorTEMPLATE.class);
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
		List<Mutation> mutationsLine6 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 6);
		List<Mutation> mutationsLine10 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 10);
		List<Mutation> mutationsLine14 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 14);
		List<Mutation> mutationsLine18 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 18);
		Long idLine6 = mutationsLine6.get(0).getId();
		Long idLine10 = mutationsLine10.get(0).getId();
		Long idLine14 = mutationsLine14.get(0).getId();
		Long idLine18 = mutationsLine18.get(0).getId();
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		coverageMap.put(idLine6, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine10, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine14, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine18, new HashSet<String>(Arrays.asList("test1")));
		MutationCoverageFile.saveCoverageData(coverageMap);
		String prefix = config.getProjectPrefix();
		config.setProjectPrefix(className);
		AddOffutt96Sufficient.addAorMutations();
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
		Method m1 = clazz.getMethod("m1", int.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 6);
		assertEquals(6, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			// Note: x > y gets translated to PUSH X, PUSH Y, IF_ICMPLE
			// Thus IF_ICMPEQ corresponds to x != y,
			// and IF_ICMPNE corresponds to x == y and so forth.
			if (addInfo == Opcodes.ISUB) {
				Integer[] input = new Integer[] { 2 };
				checkUnmutated(input, 4, m1, clazz);
				checkMutation(mutation, input, 0, m1, clazz);
			} else if (addInfo == Opcodes.IMUL) {
				Integer[] input = new Integer[] { 3 };
				checkUnmutated(input, 6, m1, clazz);
				checkMutation(mutation, input, 9, m1, clazz);
			} else if (addInfo == Opcodes.IDIV) {
				Integer[] input = new Integer[] { 2 };
				checkUnmutated(input, 4, m1, clazz);
				checkMutation(mutation, input, 1, m1, clazz);
			} else if (addInfo == Opcodes.IREM) {
				Integer[] input = new Integer[] { 4 };
				checkUnmutated(input, 8, m1, clazz);
				checkMutation(mutation, input, 0, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_SINGLE) {
				Integer[] input = new Integer[] { 4 };
				checkUnmutated(input, 8, m1, clazz);
				checkMutation(mutation, input, 4, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_SINGLE) {
				Integer[] input = new Integer[] { 2 };
				checkUnmutated(input, 4, m1, clazz);
				checkMutation(mutation, input, 2, m1, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

	@Test
	public void testMutationsLine10() throws Exception {
		Method m2 = clazz.getMethod("m2", double.class, double.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 10);
		assertEquals(6, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.DADD) {
				Double[] input = new Double[] { 2., 2. };
				checkUnmutated(input, 0., m2, clazz);
				checkMutation(mutation, input, 4., m2, clazz);
			} else if (addInfo == Opcodes.DMUL) {
				Double[] input = new Double[] { 3., 2. };
				checkUnmutated(input, 1., m2, clazz);
				checkMutation(mutation, input, 6., m2, clazz);
			} else if (addInfo == Opcodes.DDIV) {
				Double[] input = new Double[] { 2., 2. };
				checkUnmutated(input, 0., m2, clazz);
				checkMutation(mutation, input, 1., m2, clazz);
			} else if (addInfo == Opcodes.DREM) {
				Double[] input = new Double[] { 4., 2. };
				checkUnmutated(input, 2., m2, clazz);
				checkMutation(mutation, input, 0., m2, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_DOUBLE) {
				Double[] input = new Double[] { 4., 3. };
				checkUnmutated(input, 1., m2, clazz);
				checkMutation(mutation, input, 3., m2, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_DOUBLE) {
				Double[] input = new Double[] { 4., 3. };
				checkUnmutated(input, 1., m2, clazz);
				checkMutation(mutation, input, 4., m2, clazz);
			}
		}
	}

	@Test
	public void testMutationsLine14() throws Exception {
		Method m2 = clazz.getMethod("m3", long.class, long.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 14);
		assertEquals(6, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.LADD) {
				Long[] input = new Long[] { 2l, 2l };
				checkUnmutated(input, 0l, m2, clazz);
				checkMutation(mutation, input, 4l, m2, clazz);
			} else if (addInfo == Opcodes.LMUL) {
				Long[] input = new Long[] { 3l, 2l };
				checkUnmutated(input, 1l, m2, clazz);
				checkMutation(mutation, input, 6l, m2, clazz);
			} else if (addInfo == Opcodes.LDIV) {
				Long[] input = new Long[] { 2l, 2l };
				checkUnmutated(input, 0l, m2, clazz);
				checkMutation(mutation, input, 1l, m2, clazz);
			} else if (addInfo == Opcodes.LREM) {
				Long[] input = new Long[] { 4l, 2l };
				checkUnmutated(input, 2l, m2, clazz);
				checkMutation(mutation, input, 0l, m2, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_DOUBLE) {
				Long[] input = new Long[] { 4l, 3l };
				checkUnmutated(input, 1l, m2, clazz);
				checkMutation(mutation, input, 3l, m2, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_DOUBLE) {
				Long[] input = new Long[] { 4l, 3l };
				checkUnmutated(input, 1l, m2, clazz);
				checkMutation(mutation, input, 4l, m2, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

	@Test
	public void testMutationsLine18() throws Exception {
		Method m4 = clazz.getMethod("m4", float.class, float.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 18);
		assertEquals(6, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.FADD) {
				Float[] input = new Float[] { 2f, 2f };
				checkUnmutated(input, 0f, m4, clazz);
				checkMutation(mutation, input, 4f, m4, clazz);
			} else if (addInfo == Opcodes.FMUL) {
				Float[] input = new Float[] { 3f, 2f };
				checkUnmutated(input, 1f, m4, clazz);
				checkMutation(mutation, input, 6f, m4, clazz);
			} else if (addInfo == Opcodes.FDIV) {
				Float[] input = new Float[] { 2f, 2f };
				checkUnmutated(input, 0f, m4, clazz);
				checkMutation(mutation, input, 1f, m4, clazz);
			} else if (addInfo == Opcodes.FREM) {
				Float[] input = new Float[] { 4f, 2f };
				checkUnmutated(input, 2f, m4, clazz);
				checkMutation(mutation, input, 0f, m4, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_SINGLE) {
				Float[] input = new Float[] { 4f, 3f };
				checkUnmutated(input, 1f, m4, clazz);
				checkMutation(mutation, input, 3f, m4, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_SINGLE) {
				Float[] input = new Float[] { 4f, 3f };
				checkUnmutated(input, 1f, m4, clazz);
				checkMutation(mutation, input, 4f, m4, clazz);
			}else{
				fail("Did not expect mutation " + mutation);
			}
		}
	}

}
