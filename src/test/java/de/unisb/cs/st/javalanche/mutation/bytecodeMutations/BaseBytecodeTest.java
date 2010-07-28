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
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.util.collections.ArrayList;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer.RemoveSystemExitClassAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class BaseBytecodeTest {

	protected MutationObserver mutationObserver;

	protected static final String TEMPLATE_STRING = "TEMPLATE";
	protected final String simpleClassName;
	protected final String packageName;
	protected final String className;
	protected final String templateFileName;
	protected final String testClassName;

	protected boolean verbose;
	protected static String[] testCaseNames;

	protected static final File OUT_DIR = new File("target/tmp/");

	private static final int[] linenumbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12 };

	public BaseBytecodeTest(Class<?> c) {
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
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		List<Mutation> pos = scan(classFile);
		// analyze(outFile);
		redefineMutations(className);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
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
		ClassVisitor cv = new MutationsClassAdapter(cw,
				new BytecodeInfo());
		cv = new RemoveSystemExitTransformer.RemoveSystemExitClassAdapter(cv);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] result = cw.toByteArray();
		if (verbose) {
			System.out.println("---------------------------------------");
			System.out.println("Transformed class:");
			String transformed = AsmUtil.classToString(result);
			System.out.println(transformed);
		}
		FileUtils.writeByteArrayToFile(classFile, result);
	}

	private List<Mutation> scan(File classFile) throws IOException {
		byte[] b = FileUtils.readFileToByteArray(classFile);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cc = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cc = new TraceClassVisitor(cc, new PrintWriter(
					MutationPreMain.sysout));
		}
		ClassVisitor cv = new MutationsCollectorClassAdapter(cc, mpc);
		ClassReader cr = new ClassReader(b);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		mpc.toDB();
		return mpc.getPossibilities();
	}

	@Before
	public void setup() {
		mutationObserver = new MutationObserver();
		ByteCodeTestUtils.generateCoverageData(className, testCaseNames,
				linenumbers);
		ByteCodeTestUtils.deleteTestMutationResult(className);
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
		int result = compiler.run(null, out, err, "-g", outFile
				.getAbsolutePath());
		assertEquals("Compiler failed:\nOut:" + out.toString() + "\nErr:"
				+ err.toString(), 0, result);

	}

	protected void checkUnmutated(Object expectedOutput, Method method,
			Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object invoke = method.invoke(instance);
		assertEquals("Expected different result for unmutated run of method "
				+ method + ".", expectedOutput, invoke);

	}

	protected void checkUnmutated(int input, Object expectedOutput,
			Method method, Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object invoke = method.invoke(instance, input);
		assertEquals("Expected different result for unmutated run of method "
				+ method + " with input " + input + ".", expectedOutput, invoke);
	}

	protected void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, Object expectedResult, Method method,
			Class<?> clazz) throws Exception {
		checkMutation(lineNumber, type, mutationForLine, new Object[0],
				expectedResult, method, clazz);

	}

	protected void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, int input, Object expectedResult,
			Method method, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		checkMutation(lineNumber, type, mutationForLine,
				new Object[] { input }, expectedResult, method, clazz);
	}

	protected void checkMutation(int lineNumber, MutationType type,
			int mutationForLine, Object[] input, Object expectedResult,
			Method method, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Mutation queryMutation = new Mutation(className, method.getName(),
				lineNumber, mutationForLine, type);
		Mutation m = QueryManager.getMutation(queryMutation);
		System.setProperty(m.getMutationVariable(), "true");
		mutationObserver.mutationStart(m);
		Object instance = clazz.newInstance();
		Object result = method.invoke(instance, input);
		mutationObserver.mutationEnd(m);
		System.clearProperty(m.getMutationVariable());
		assertEquals(
				"Expected different result when mutation is enabled. Mutation"
						+ m, expectedResult, result);
	}

}
