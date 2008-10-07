package org.softevo.mutation.testutil;

import java.io.IOException;
import java.io.InputStream;


import org.objectweb.asm.ClassReader;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.debug.MissedMutationTest;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class UtilTest {

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
