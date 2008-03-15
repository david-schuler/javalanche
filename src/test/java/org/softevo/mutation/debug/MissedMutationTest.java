package org.softevo.mutation.debug;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MissedMutationTest {

	
	@Test
	public void testLangUtil() {
		InputStream is = MissedMutationTest.class.getClassLoader()
				.getResourceAsStream("LangUtil.clazz");
		try {
			MutationPossibilityCollector mpc = new MutationPossibilityCollector();
			BytecodeTransformer bt = new MutationScannerTransformer(mpc);
			bt.transformBytecode(new ClassReader(is));
			Assert.assertTrue(mpc.size() > 40);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
