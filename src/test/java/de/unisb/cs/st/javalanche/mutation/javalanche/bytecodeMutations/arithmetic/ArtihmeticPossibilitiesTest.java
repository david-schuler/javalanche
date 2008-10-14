package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import de.unisb.cs.st.javalanche.mutation.debug.MissedMutationTest;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class ArtihmeticPossibilitiesTest {

	@Test
	public void testNegateJumps() {
		InputStream is = MissedMutationTest.class.getClassLoader()
				.getResourceAsStream(TestProperties.ADVICE_CLAZZ);
		System.out.println(is);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		BytecodeTransformer bt = new ArithmeticReplaceCollectorTransformer(mpc);
		try {
			bt.transformBytecode(new ClassReader(is));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(mpc.size() > 10);
	}
}
