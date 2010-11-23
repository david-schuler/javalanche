package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.ClassReader;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class MethodNamesTest {

	@Test
	public void testMethodNames() {
		InputStream is = MethodNamesTest.class.getClassLoader()
				.getResourceAsStream(
						"de.unisb.cs.st.javalanche.mutation.bytecodeMutations."
								.replace('.', '/')
								+ "MethodNamesTestData.class");
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		BytecodeTransformer bt = new MutationScannerTransformer(mpc);
		try {
			bt.transformBytecode(new ClassReader(is));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		List<Mutation> mutations = mpc.getPossibilities();
		boolean found[] = new boolean[4];
		for (Mutation m : mutations) {
			if (m.getLineNumber() == 8) {
				found[0] = true;
				System.out.println(m.getMethodName());
				assertEquals("<init>()V", m.getMethodName());
			}
			if (m.getLineNumber() == 12) {
				found[1] = true;
				System.out.println(m.getMethodName());
				assertEquals("<init>(I)V", m.getMethodName());
			}
			if (m.getLineNumber() == 16) {
				found[2] = true;
				assertEquals("dummy()V", m.getMethodName());
			}
			if (m.getLineNumber() == 20) {
				found[3] = true;
				assertEquals("dummy2(ILjava/lang/String;Ljava/io/File;)V", m
						.getMethodName());
			}
		}
		for (boolean b : found) {
			assertTrue("Expected mutation to be found", b);
		}
	}

}
