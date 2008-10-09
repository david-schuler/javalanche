package de.st.cs.unisb.javalanche.testutil;

import java.io.IOException;
import java.io.InputStream;


import org.objectweb.asm.ClassReader;
import de.st.cs.unisb.javalanche.bytecodeMutations.MutationScannerTransformer;
import de.st.cs.unisb.javalanche.debug.MissedMutationTest;
import de.st.cs.unisb.javalanche.mutationPossibilities.MutationPossibilityCollector;

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
