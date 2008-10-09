package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.TestProperties;

public class NegateJumpsPossibilitiesTest {

	@Test
	public void testPossibilities() throws Exception {
		File file = new File(TestProperties.SAMPLE_FILE);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		NegateJumpsPossibilitiesClassAdapter njpcv = new NegateJumpsPossibilitiesClassAdapter(cw, mpc);
		cr.accept(njpcv, 0);
		Assert.assertTrue(mpc.size() > 40);
	}

}
