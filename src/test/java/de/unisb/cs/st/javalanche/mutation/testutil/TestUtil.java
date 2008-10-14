package de.unisb.cs.st.javalanche.mutation.testutil;

import java.io.IOException;
import java.io.InputStream;


import org.objectweb.asm.ClassReader;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.debug.MissedMutationTest;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class TestUtil {



	public static void getMutationsForClazzOnClasspath(String fileName) {
		InputStream is = MissedMutationTest.class.getClassLoader()
				.getResourceAsStream(fileName);
		try {
			MutationPossibilityCollector mpc = new MutationPossibilityCollector();
			BytecodeTransformer bt = new MutationScannerTransformer(mpc);
			bt.transformBytecode(new ClassReader(is));
			mpc.toDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
