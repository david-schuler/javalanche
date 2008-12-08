package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCalls;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class RemoveMethodCallsPossibilitiesTest {

	@Test
	public void testForOneClass() throws Exception {
		String className = MethodCalls.class.getName();
		String resourceName = className.replace('.', '/') + ".class";
		URL systemResource = ClassLoader.getSystemResource(resourceName);
		File file = new File(systemResource.toURI());
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(mcca, 0);
		// System.out.println(mutationPossibilityCollector.size());
		List<Mutation> possibilies = mutationPossibilityCollector
				.getPossibilities();
		int possibilityCount = 0;
		for (Mutation mutation : possibilies) {
			if (mutation.getMutationType().equals(MutationType.REMOVE_CALL)) {
				possibilityCount++;
			}
		}
		int expectedMutations = 4;
		assertEquals("Expecting " + expectedMutations
				+ " mutations that remove calls", expectedMutations,
				possibilityCount);
	}
}
