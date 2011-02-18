package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.Triangle2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class Triangle2Test {

	private static JavalancheConfiguration configBack;
	private static JavalancheTestConfiguration config;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
	}

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}

	@Test
	public void testStaticIntsClass() throws Exception {
		config.setMutationType(MutationType.REPLACE_VARIABLE, true);

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


}
