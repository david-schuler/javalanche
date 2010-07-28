package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;
import junit.framework.TestResult;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.JumpsPossibilitiesClassAdapter;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace.ReplacePossibilitiesClassAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;

public class TestScan {

	// @Test
	public void testScan() {
		MutationProperties.RUN_MODE = RunMode.SCAN;
		MutationProperties.PROJECT_PREFIX = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.classes";
		MutationProperties.TEST_SUITE = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes.AllTests";
		de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes.AllTests
				.suite().run(new TestResult());

	}

	@Test
	public void testPossibilitiesReplace() throws Exception {
		File file = new File(TestProperties.SAMPLE_FILE);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		CheckClassAdapter cv = new CheckClassAdapter(cw);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ReplacePossibilitiesClassAdapter rp = new ReplacePossibilitiesClassAdapter(
				cv, mpc);
		cr.accept(rp, 0);

		Assert.assertTrue(mpc.size() > 40);
	}

	@Test
	public void testPossibilitiesJumps() throws Exception {
		File file = new File(TestProperties.SAMPLE_FILE);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		CheckClassAdapter cv = new CheckClassAdapter(cw);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		JumpsPossibilitiesClassAdapter rp = new JumpsPossibilitiesClassAdapter(
				cv, mpc);
		cr.accept(rp, 0);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Assert.assertTrue(mpc.size() > 40);
	}
}
