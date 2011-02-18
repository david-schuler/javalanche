package de.unisb.cs.st.javalanche.mutation.javaagent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class MutationScannerTest {

	private static byte[] byteArray;
	private static String className;

	static class MyClassAdapter extends ClassAdapter {

		private String owner;

		public MyClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			owner = name;
			super.visit(version, access, name, signature, superName, interfaces);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {

			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			AnalyzerAdapter aa = new AnalyzerAdapter(owner, access, name, desc,
					mv);
			return aa;
		}
	}

	@BeforeClass
	public static void setUpClass() {
		className = TestProperties.SIMPLE_FUNCTION_CONTEXT_CLASS
				.getClassName();
		QueryManager.deleteMutations(className);

		byteArray = TestProperties.SIMPLE_FUNCTION_CONTEXT_CLASS
				.getClassBytes();

		File dir = new File("mutation-files/");
		dir.mkdir();
		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(className.replace('.', '/'), byteArray);
		sTransformer.write();
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
		config.setTestNames("noTest");
		config.setProjectPrefix("org.jaxen");
	}

	private static JavalancheConfiguration configBack;
	private static JavalancheTestConfiguration config;

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);

	}


	@Test
	public void testAnalyzeAdapter() throws IOException,
			IllegalClassFormatException {
		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(className.replace('.', '/'), byteArray);
		sTransformer.write();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MutationPossibilityCollector mp = new MutationPossibilityCollector();
		ClassVisitor cv = cw;
		// TraceClassVisitor tcv = new TraceClassVisitor(cv, new PrintWriter(
		// System.out));
		// cv = tcv;
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cv, mp);
		ClassReader cr = new ClassReader(byteArray);
		cr.accept(mcca, ClassReader.EXPAND_FRAMES);
		byte[] transformed = cw.toByteArray();
		AsmUtil.checkClass2(transformed);
	}

	@Test
	public void testScan() throws IOException, IllegalClassFormatException {
		MutationScanner ms = new MutationScanner();
		ms.transform(null, className, null, null, byteArray);

	}
}
