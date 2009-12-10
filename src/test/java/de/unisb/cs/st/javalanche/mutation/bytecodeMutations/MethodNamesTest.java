package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MethodNamesTest {

	public void dummy() {
		int i = 111 + 10; // Line 22
	}

	public void dummy2(int i, String s, File f) {
		dummy(); // Line 26
	}

	@Test
	public void testMethodNames() {
		InputStream is = MethodNamesTest.class
				.getClassLoader()
				.getResourceAsStream(
						"de.unisb.cs.st.javalanche.mutation.bytecodeMutations."
								.replace('.', '/')
								+ "MethodNamesTest.class");
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		BytecodeTransformer bt = new MutationScannerTransformer(mpc);
		try {
			bt.transformBytecode(new ClassReader(is));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		List<Mutation> mutations = mpc.getPossibilities();
		boolean found1 = false;
		boolean found2 = false;
		for (Mutation m : mutations) {
			if (m.getLineNumber() == 22) {
				found1 = true;
				assertEquals("dummy()V", m.getMethodName());
			}
			if (m.getLineNumber() == 26) {
				found2 = true;
				assertEquals("dummy2(ILjava/lang/String;Ljava/io/File;)V", m
						.getMethodName());
			}
		}
		assertTrue(found1);
		assertTrue(found2);
	}

}
