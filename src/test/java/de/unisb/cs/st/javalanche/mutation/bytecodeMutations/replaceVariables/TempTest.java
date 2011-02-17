package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.Triangle2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class TempTest {

	@BeforeClass
	public static void setUpClass() {
		MutationProperties.ENABLE_REPLACE_VARIABLES = true;
	}

	@AfterClass
	public static void tearDownClass() {
		MutationProperties.ENABLE_REPLACE_VARIABLES = false;
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
		// System.out.println(filteredMutations);
		assertEquals(4, TestUtil.filterMutations(filteredMutations, 6).size());
		assertEquals(2, TestUtil.filterMutations(filteredMutations, 7).size());
		assertEquals(3, TestUtil.filterMutations(filteredMutations, 8).size());
		assertEquals(3, TestUtil.filterMutations(filteredMutations, 9).size());
		assertEquals(4, TestUtil.filterMutations(filteredMutations, 12).size());
		assertEquals(58, res);
	}

	private void scan(String name, byte[] bytes) {
		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(name, bytes);
		sTransformer.write();

		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cc = cw;
		// ClassVisitor cc = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cc = new TraceClassVisitor(cc, new PrintWriter(
					MutationPreMain.sysout));
		}
		ClassVisitor cv = new MutationsCollectorClassAdapter(cc, mpc);
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		mpc.toDB();

		List<Mutation> ms = new ArrayList<Mutation>(mpc.getPossibilities());
		Collections.sort(ms);
		System.out.println(ms);
		assertEquals(78, mpc.size());
	}
}
