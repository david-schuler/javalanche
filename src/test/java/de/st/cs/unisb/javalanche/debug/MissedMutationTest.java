package de.unisb.cs.st.javalanche.mutation.debug;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MissedMutationTest {

	
	@Test
	public void testLangUtil() {
		InputStream is = MissedMutationTest.class.getClassLoader()
				.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
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
