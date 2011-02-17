package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

//import static junit.framework.Assert.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import java.util.ArrayList;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariableClass1;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariables2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariables6TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class ReplaceVariablesPosibilitiesTest {

	@BeforeClass
	public static void setUpClass() {
		MutationProperties.ENABLE_REPLACE_VARIABLES = true;
	}

	@AfterClass
	public static void tearDownClass() {
		MutationProperties.ENABLE_REPLACE_VARIABLES = false;
	}

	@Test
	public void testStaticIntsClass() throws Exception {
		List<Mutation> rvMutations = scanForReplaceVariableMutations(ReplaceVariableClass1.class);
		Collections.sort(rvMutations);

		int possibilityCount = rvMutations.size();
		int expectedMutations = 2;
		assertEquals("Expecting different number of mutations for class "
				+ RemoveCallsTEMPLATE.class, expectedMutations,
				possibilityCount);

		Mutation m1 = rvMutations.get(0);
		Mutation m2 = rvMutations.get(1);
		assertEquals(12, m1.getLineNumber());
		assertEquals(12, m2.getLineNumber());
		assertEquals(MutationType.REPLACE_VARIABLE, m1.getMutationType());
		assertEquals(MutationType.REPLACE_VARIABLE, m2.getMutationType());

		System.out.println(m1);
		System.out.println(m2);
		assertThat(m1.getAddInfo(), containsString("a with b"));
		assertThat(m2.getAddInfo(), containsString("a with c"));

	}

	@Test
	public void testStaticMixedTypes1Class() throws Exception {
		List<Mutation> rvMutations = scanForReplaceVariableMutations(ReplaceVariables2TEMPLATE.class);
		int possibilityCount = rvMutations.size();
		int expectedMutations = 2;
		assertEquals("Expecting different number of mutations for class "
				+ RemoveCallsTEMPLATE.class, expectedMutations,
				possibilityCount);

		Mutation m1 = rvMutations.get(0);
		Mutation m2 = rvMutations.get(1);
		System.out.println(m1);
		System.out.println(m2);
		assertEquals(MutationType.REPLACE_VARIABLE, m1.getMutationType());
		assertEquals(MutationType.REPLACE_VARIABLE, m2.getMutationType());
		assertEquals(14, m1.getLineNumber());
		assertEquals(18, m2.getLineNumber());

	}

	@Test
	public void testLocalMixedObjectsClass() throws Exception {
		List<Mutation> rvMutations = scanForReplaceVariableMutations(ReplaceVariables6TEMPLATE.class);
		int possibilityCount = rvMutations.size();
		int expectedMutations = 0;
		System.out.println(rvMutations);
		assertEquals("Expecting different number of mutations for class "
				+ RemoveCallsTEMPLATE.class, expectedMutations,
				possibilityCount);
	}

	private List<Mutation> scanForReplaceVariableMutations(Class clazz)
			throws URISyntaxException, IOException, FileNotFoundException {
		File file = getFileForClass(clazz);

		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(clazz.getCanonicalName().replace('.', '/'),
				new ClassReader(new FileInputStream(file)));
		sTransformer.write();

		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(mcca, 0);
		List<Mutation> possibilies = mutationPossibilityCollector
				.getPossibilities();
		List<Mutation> rvMutations = getReplaceVariableMutations(possibilies);
		return rvMutations;
	}

	private List<Mutation> getReplaceVariableMutations(List<Mutation> mutations) {
		List<Mutation> rvMutations = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.getMutationType()
					.equals(MutationType.REPLACE_VARIABLE)) {
				rvMutations.add(mutation);
			}
		}
		Collections.sort(rvMutations);
		return rvMutations;
	}

	private File getFileForClass(Class clazz) throws URISyntaxException {
		String className = clazz.getName();
		String resourceName = className.replace('.', '/') + ".class";
		URL systemResource = ClassLoader.getSystemResource(resourceName);
		File file = new File(systemResource.toURI());
		return file;
	}

}
