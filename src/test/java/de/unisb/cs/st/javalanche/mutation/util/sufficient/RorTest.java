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

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AddOffutt96Sufficient;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.RorTEMPLATE;

public class RorTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public RorTest() throws Exception {
		super(RorTEMPLATE.class);
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class, int.class);
		checkUnmutated(new Integer[] { 1, 2 }, false, m1, clazz);
		checkMutation(6, MutationType.NEGATE_JUMP, 0, new Integer[] { 1, 2 },
				true, m1, clazz);
	}


	@Test
	public void testNumberOfMutations() throws Exception {
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 6);
		assertEquals(1, mutations.size());

		List<Mutation> mutationsLine10 = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 10);
		assertEquals(1, mutationsLine10.size());

		JavalancheConfiguration
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
		config.setProjectPrefix("de.unisb.cs.st.javalanche.mutation.util.sufficient");
		Long id = mutations.get(0).getId();
		Long idLine10 = mutationsLine10.get(0).getId();
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		coverageMap.put(id, new HashSet<String>(Arrays.asList("test1")));
		coverageMap.put(idLine10, new HashSet<String>(Arrays.asList("test1")));
		MutationCoverageFile.saveCoverageData(coverageMap);
		AddOffutt96Sufficient.addRorMutations();
		List<Mutation> mutationsAfter = QueryManager.getMutations(className,
				MutationType.NEGATE_JUMP, 6);
		assertEquals(7, mutationsAfter.size());

		List<Mutation> mutationsLine10After = QueryManager.getMutations(
				className, MutationType.NEGATE_JUMP, 10);
		assertEquals(7, mutationsLine10After.size());

		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", int.class);
		checkUnmutated(new Integer[] { 1 }, true, m2, clazz);
		checkUnmutated(new Integer[] { -1 }, false, m2, clazz);
		checkMutation(10, MutationType.NEGATE_JUMP, 0, new Integer[] { 1 },
				false, m2, clazz);
	}
}
