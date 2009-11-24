package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import static org.junit.Assert.*;

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
		int i = 111 + 10; // Line 21
	}

	public void dummy2() {
		dummy(); // Line 25
	}

	@Test
	public void testPossibilities() {
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
			if (m.getLineNumber() == 21) {
				found1 = true;
				assertEquals("dummy", m.getMethodName());
			}
			if (m.getLineNumber() == 25) {
				found2 = true;
				assertEquals("dummy2", m.getMethodName());
			}
		}
		assertTrue(found1);
		assertTrue(found2);
	}

}
