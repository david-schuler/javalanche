package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;
import java.io.FileInputStream;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.TestProperties;

public class RicPossibilitiesTest {

	@Test
	public void testForOneClass() throws Exception {
		File file = new File(TestProperties.SAMPLE_FILE);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		PossibilitiesRicClassAdapter possibilitiesRicClassAdapter = new PossibilitiesRicClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(possibilitiesRicClassAdapter, 0);
		System.out.println(mutationPossibilityCollector.size());
		int expectedMutations = 140;
		assertEquals("Expecting " + expectedMutations + " mutations",
				mutationPossibilityCollector.size(), expectedMutations);
	}

}
