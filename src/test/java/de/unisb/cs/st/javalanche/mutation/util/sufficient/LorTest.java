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
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.LorTEMPLATE;

public class LorTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public LorTest() throws Exception {
		super(LorTEMPLATE.class);
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
		// List<Mutation> mutationsLine14 = QueryManager.getMutations(className,
		// MutationType.ARITHMETIC_REPLACE, 14);
		// List<Mutation> mutationsLine18 = QueryManager.getMutations(className,
		// MutationType.ARITHMETIC_REPLACE, 18);
		Long idLine6 = mutationsLine6.get(0).getId();
		Long idLine10 = mutationsLine10.get(0).getId();
		// Long idLine14 = mutationsLine14.get(0).getId();
		// Long idLine18 = mutationsLine18.get(0).getId();
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		coverageMap.put(idLine6, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine10, new HashSet<String>(Arrays.asList("test1")));
		// coverageMap.put(idLine14, new
		// HashSet<String>(Arrays.asList("test1")));
		// coverageMap.put(idLine18, new
		// HashSet<String>(Arrays.asList("test1")));
		MutationCoverageFile.saveCoverageData(coverageMap);
		String prefix = config.getProjectPrefix();
		config.setProjectPrefix("de.unisb.cs.st.javalanche.mutation.util.sufficient");
		AddOffutt96Sufficient.addLorMutations();
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
				MutationType.ARITHMETIC_REPLACE, 6);
		assertEquals(4, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.IOR) {
				Integer[] input = new Integer[] { 0x2, 0x4 };
				checkUnmutated(input, 0x0, m1, clazz);
				checkMutation(mutation, input, 0x6, m1, clazz);
			} else if (addInfo == Opcodes.IXOR) {
				Integer[] input = new Integer[] { 0x1, 0x1 };
				checkUnmutated(input, 0x1, m1, clazz);
				checkMutation(mutation, input, 0x0, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_SINGLE) {
				Integer[] input = new Integer[] { 0x2, 0x4 };
				checkUnmutated(input, 0x0, m1, clazz);
				checkMutation(mutation, input, 0x4, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_SINGLE) {
				Integer[] input = new Integer[] { 0x2, 0x4 };
				checkUnmutated(input, 0x0, m1, clazz);
				checkMutation(mutation, input, 0x2, m1, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

	@Test
	public void testMutationsLine10() throws Exception {
		Method m1 = clazz.getMethod("m2", long.class, long.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 10);
		assertEquals(4, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == Opcodes.LOR) {
				Long[] input = new Long[] { 0x3l, 0x5l };
				checkUnmutated(input, 0x6l, m1, clazz);
				checkMutation(mutation, input, 0x7l, m1, clazz);
			} else if (addInfo == Opcodes.LAND) {
				Long[] input = new Long[] { 0x1l, 0x1l };
				checkUnmutated(input, 0x0l, m1, clazz);
				checkMutation(mutation, input, 0x1l, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_LEFT_VALUE_DOUBLE) {
				Long[] input = new Long[] { 0x2l, 0x4l };
				checkUnmutated(input, 0x6l, m1, clazz);
				checkMutation(mutation, input, 0x4l, m1, clazz);
			} else if (addInfo == AbstractArithmeticMethodAdapter.REMOVE_RIGHT_VALUE_DOUBLE) {
				Long[] input = new Long[] { 0x2l, 0x4l };
				checkUnmutated(input, 0x6l, m1, clazz);
				checkMutation(mutation, input, 0x2l, m1, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

}