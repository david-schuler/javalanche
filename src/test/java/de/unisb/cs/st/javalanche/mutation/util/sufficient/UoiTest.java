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
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.UoiTEMPLATE;

public class UoiTest extends BaseBytecodeTest {

	private Class<?> clazz;

	private static final int[] lineNumbers = { 6, 11, 16 };

	public UoiTest() throws Exception {
		super(UoiTEMPLATE.class);
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
		addCoverageData(lineNumbers);
		String prefix = config.getProjectPrefix();
		config.setProjectPrefix("de.unisb.cs.st.javalanche.mutation.util.sufficient");
		AddOffutt96Sufficient.addUoiForConstants();
		config.setProjectPrefix(prefix);
		//

		redefineMutations(className, config);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		ConfigurationLocator.setJavalancheConfiguration(configBack);
		return clazz;
	}

	public void addCoverageData(int[] lineNumbers) {
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		for (int line : lineNumbers) {
			List<Mutation> m = QueryManager.getMutations(className,
					MutationType.REPLACE_CONSTANT, line);
			Long id = m.get(0).getId();
			coverageMap.put(id, new HashSet<String>(Arrays.asList("test1")));
		}
		MutationCoverageFile.saveCoverageData(coverageMap);
	}

	@Test
	public void testMutationsLine6() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.REPLACE_CONSTANT, 6);
		assertEquals(2, mutations.size());
		for (Mutation mutation : mutations) {
			int addInfo = Integer.parseInt(mutation.getOperatorAddInfo());
			if (addInfo == -2) {
				Integer[] input = new Integer[] { 2 };
				checkUnmutated(input, 4, m1, clazz);
				checkMutation(mutation, input, -4, m1, clazz);
			} else if (addInfo == ~2) {
				Integer[] input = new Integer[] { 3 };
				checkUnmutated(input, 6, m1, clazz);
				checkMutation(mutation, input, ~2 * 3, m1, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

	@Test
	public void testMutationsLine11() throws Exception {
		Method m2 = clazz.getMethod("m2", double.class);
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.REPLACE_CONSTANT, 11);
		assertEquals(1, mutations.size());
		for (Mutation mutation : mutations) {
			double addInfo = Double.parseDouble(mutation.getOperatorAddInfo());
			if (addInfo == -2d) {
				Double[] input = new Double[] { 5d };
				checkUnmutated(input, (Double) 10d, m2, clazz);
				checkMutation(mutation, input, (Double) (-10d), m2, clazz);
			} else {
				fail("Did not expect mutation " + mutation);
			}
		}
	}

}