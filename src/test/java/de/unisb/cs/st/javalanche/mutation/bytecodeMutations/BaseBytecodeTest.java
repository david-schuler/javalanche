package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.Triangle2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.DebugProperties;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class BaseBytecodeTest {

	protected MutationObserver mutationObserver;

	protected static final String TEMPLATE_STRING = "TEMPLATE";
	protected String simpleClassName;
	protected String packageName;
	protected String className;
	protected String templateFileName;
	protected String testClassName;

	protected boolean verbose;

	protected static String[] testCaseNames;

	protected static final File OUT_DIR = new File("target/tmp/");

	private static final int[] linenumbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12 };

	protected JavalancheTestConfiguration config = new JavalancheTestConfiguration();

	public BaseBytecodeTest(Class<?> c) {
		initVariables(c);
		mutationObserver = new MutationObserver();
		ByteCodeTestUtils.generateCoverageData(className, testCaseNames,
				linenumbers);
		ByteCodeTestUtils.deleteTestMutationResult(className);
	}

	public void initVariables(Class<?> c) {
		String name = c.getName();
		className = name.replace(TEMPLATE_STRING, "");
		simpleClassName = className.substring(className.lastIndexOf('.') + 1);
		packageName = c.getPackage().getName();
		templateFileName = "src/test/java/" + className.replace('.', '/')
				+ TEMPLATE_STRING + ".java";
		testClassName = className + "Test";
		testCaseNames = ByteCodeTestUtils.generateTestCaseNames(testClassName,
				5);
	}

	public Class<?> prepareTest() throws Exception {
		JavalancheConfiguration configBack = ConfigurationLocator
				.getJavalancheConfiguration();

		ConfigurationLocator.setJavalancheConfiguration(config);
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		List<Mutation> pos = scan(classFile);
		System.out.println("SCAN: " + pos.size());
		// analyze(outFile);
		redefineMutations(className, config);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		ConfigurationLocator.setJavalancheConfiguration(configBack);
		return clazz;
	}

	protected void transform(File classFile) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(classFile);
		if (verbose) {
			System.out.println("Original class:");
			String orig = AsmUtil.classToString(bytes);
			System.out.println(orig);
		}
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cv = new MutationsClassAdapter(cw, new MutationManager());
		cv = new RemoveSystemExitTransformer.RemoveSystemExitClassAdapter(cv);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		byte[] result = cw.toByteArray();
		if (verbose) {
			System.out.println("---------------------------------------");
			System.out.println("Transformed class:");
			String transformed = AsmUtil.classToString(result);
			System.out.println(transformed);
		}
		FileUtils.writeByteArrayToFile(classFile, result);
	}

	protected List<Mutation> scan(File classFile) throws IOException {
		byte[] b = FileUtils.readFileToByteArray(classFile);
		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(className.replace('.', '/'), b);
		sTransformer.write();

		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cc = cw;
		// ClassVisitor cc = new CheckClassAdapter(cw);
		if (DebugProperties.TRACE_BYTECODE) {
			cc = new TraceClassVisitor(cc, new PrintWriter(
					MutationPreMain.sysout));
		}
		ClassVisitor cv = new MutationsCollectorClassAdapter(cc, mpc);
		ClassReader cr = new ClassReader(b);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		mpc.toDB();
		return mpc.getPossibilities();
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(className);
		ByteCodeTestUtils.deleteCoverageData(className);
	}

	protected void check(int lineNumber, int input, int expectedOutput,
			int expectedMutations, Method method, Class<?> clazz)
			throws Exception {
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		check(lineNumber, input, expectedOutput, expectedMutations, method,
				mutationsForClass, clazz, null);
	}

	protected void check(int lineNumber, int input, int expectedOutput,
			int expectedMutations, Method method,
			List<Mutation> mutationsForClass, Class<?> clazz) throws Exception {
		check(lineNumber, input, expectedOutput, expectedMutations, method,
				mutationsForClass, clazz, null);
	}

	protected void check(int lineNumber, int input, int expectedOutput,
			int expectedMutations, Method method,
			List<Mutation> mutationsForClass, Class<?> clazz, MutationType type)
			throws Exception {

		int number = 0;
		for (Mutation m : mutationsForClass) {
			if (type == null || type == m.getMutationType()) {
				if (m.getLineNumber() == lineNumber) {
					System.setProperty(m.getMutationVariable(), "true");
					System.out.println("TestSkipElse.checkMutation() TESTING "
							+ m.getId());
					mutationObserver.mutationStart(m);
					Object instance = clazz.newInstance();
					Object invoke = method.invoke(instance, input);
					mutationObserver.mutationEnd(m);
					System.clearProperty(m.getMutationVariable());
					assertEquals(expectedOutput, invoke);
					number++;
				}
			}
		}
		assertEquals("Expected different number of mutations for line "
				+ lineNumber, expectedMutations, number);
	}

	protected Class<?> loadClass(File outDir) throws MalformedURLException,
			ClassNotFoundException {
		URLClassLoader loader = new URLClassLoader(new URL[] { OUT_DIR.toURI()
				.toURL() });
		Class<?> clazz = loader.loadClass(className);
		return clazz;
	}

	protected File createTmpJavaFile(String fileToCopy, File outDir)
			throws IOException {
		File file = new File(fileToCopy);
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(file);
		List<String> writeLines = new ArrayList<String>();
		for (String l : lines) {
			if (l.contains(TEMPLATE_STRING)) {
				l = l.replace(TEMPLATE_STRING, "");
			}
			writeLines.add(l);
		}
		if (outDir.exists()) {
			FileUtils.deleteDirectory(outDir);
		}
		outDir.mkdirs();
		File outFile = new File(outDir, simpleClassName + ".java");
		FileUtils.writeLines(outFile, writeLines);
		return outFile;
	}

	protected void compileTest(File outFile) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int result = compiler.run(null, out, err, "-g",
				outFile.getAbsolutePath());
		assertEquals(
				"Compiler failed:\nOut:" + out.toString() + "\nErr:"
						+ err.toString(), 0, result);

	}

	public <T> void checkUnmutated(Matcher<T> matcher, Method method,
			Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object result = method.invoke(instance);

		// if (result.getClass() instanceof T) {
		T castedRes = (T) result;
		assertThat(castedRes, matcher);
		// }
	}

	public void checkUnmutated(Object expectedOutput, Method method,
			Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object invoke = method.invoke(instance);
		assertEquals("Expected different result for unmutated run of method "
				+ method + ".", expectedOutput, invoke);

	}

	public void checkUnmutated(Object input, Object expectedOutput,
			Method method, Class<?> clazz) throws Exception {
		checkUnmutated(new Object[] { input }, expectedOutput, method, clazz);
	}

	public void checkUnmutated(Object[] input, Object expectedOutput,
			Method method, Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object invoke = method.invoke(instance, input);
		if (expectedOutput.getClass().equals(Double.class)) {
			assertEquals(
					"Expected different result for unmutated run of method "
							+ method + " with input " + input + ".",
					(Double) expectedOutput, (Double) invoke, 1.e-3);
		} else {
			assertEquals(
					"Expected different result for unmutated run of method "
							+ method + " with input " + input + ".",
					expectedOutput, invoke);
		}
	}

	// protected void checkUnmutated(int input, Object expectedOutput,
	// Method method, Class<?> clazz) throws Exception {
	// Object instance = clazz.newInstance();
	// Object invoke = method.invoke(instance, input);
	// assertEquals("Expected different result for unmutated run of method "
	// + method + " with input " + input + ".", expectedOutput, invoke);
	// }

	public void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, Object expectedResult, Method method,
			Class<?> clazz) throws Exception {
		checkMutation(lineNumber, type, mutationForLine, new Object[0],
				expectedResult, method, clazz);

	}

	public void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, int input, Object expectedResult,
			Method method, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		checkMutation(lineNumber, type, mutationForLine,
				new Object[] { input }, expectedResult, method, clazz);
	}

	public void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, Object[] input, Object expectedResult,
			Method method, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Mutation queryMutation = new Mutation(className, method.getName(),
				lineNumber, mutationForLine, type);
		checkMutation(queryMutation, input, expectedResult, method, clazz);
	}

	public void checkMutation(Mutation mutation, Object input,
			Object expectedResult, Method method, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		checkMutation(mutation, new Object[] { input }, expectedResult, method,
				clazz);
	}

	public void checkMutation(Mutation mutation, Object expectedResult,
			Method method, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		checkMutation(mutation, new Object[0], expectedResult, method, clazz);
	}

	public void checkMutation(Mutation mutation, Object[] input,
			Object expectedResult, Method method, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Mutation m = QueryManager.getMutation(mutation);
		System.setProperty(m.getMutationVariable(), "true");
		mutationObserver.mutationStart(m);
		Object instance = clazz.newInstance();
		Object result = null;
		try {
			result = method.invoke(instance, input);
		} finally {
			mutationObserver.mutationEnd(m);
			System.clearProperty(m.getMutationVariable());
			boolean wasExecuted = MutationObserver.getTouchedMutations()
					.contains(m);
			assertTrue("Mutation was not covered " + m, wasExecuted);
		}
		String message = "Expected different result when mutation is enabled. Mutation"
				+ m;
		if (expectedResult.getClass().equals(Double.class)) {
			assertEquals(message, (Double) expectedResult, (Double) result,
					1e-3);
		} else {
			assertEquals(message, expectedResult, result);
		}
	}

	public <T> void checkMutationWithMatcher(Mutation mutation, Object[] input,
			Matcher<T> matcher, Method method, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Mutation m = QueryManager.getMutation(mutation);
		System.setProperty(m.getMutationVariable(), "true");
		mutationObserver.mutationStart(m);
		Object instance = clazz.newInstance();
		Object result = null;
		try {
			if (input == null) {
				result = method.invoke(instance);
			} else {
				result = method.invoke(instance, input);
			}
		} finally {
			mutationObserver.mutationEnd(m);
			System.clearProperty(m.getMutationVariable());
		}
		boolean wasExecuted = MutationObserver.getTouchedMutations()
				.contains(m);
		assertTrue("Mutation was not covered " + m, wasExecuted);
		assertThat((T) result, matcher);
	}

	public JavalancheTestConfiguration getConfig() {
		return config;
	}

	public void setConfig(JavalancheTestConfiguration config) {
		this.config = config;
	}

}
