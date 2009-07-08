package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static junit.framework.Assert.*;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
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
		File file = getFileForClass(MethodCalls.class);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(mcca, 0);
		List<Mutation> possibilies = mutationPossibilityCollector
				.getPossibilities();
		int possibilityCount = getRemoveCallMutations(possibilies);
		int expectedMutations = 4;
		assertEquals("Expecting different number of mutations for class "
				+ MethodCalls.class, expectedMutations, possibilityCount);
	}

	@Test
	public void testIgnoreSystemExit() throws Exception {
		String className = "./target/classes/de/unisb/cs/st/javalanche/mutation/bytecodeMutations/removeSystemExit/testclasses/SysExit.class";
		File f = new File(className);
		ClassReader cr = new ClassReader(new FileInputStream(f));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(mcca, ClassReader.SKIP_FRAMES);
		List<Mutation> possibilies = mutationPossibilityCollector
				.getPossibilities();
		int possibilityCount = getRemoveCallMutations(possibilies);
		int expectedMutations = 1;
		assertEquals("Expecting different number of mutations for class "
				+ className, expectedMutations, possibilityCount);

	}

	private int getRemoveCallMutations(List<Mutation> mutations) {
		int possibilityCount = 0;
		for (Mutation mutation : mutations) {
			if (mutation.getMutationType().equals(MutationType.REMOVE_CALL)) {
				possibilityCount++;

			}
		}
		return possibilityCount;
	}

	private File getFileForClass(Class clazz) throws URISyntaxException {
		String className = clazz.getName();
		String resourceName = className.replace('.', '/') + ".class";
		URL systemResource = ClassLoader.getSystemResource(resourceName);
		File file = new File(systemResource.toURI());
		return file;
	}
}
