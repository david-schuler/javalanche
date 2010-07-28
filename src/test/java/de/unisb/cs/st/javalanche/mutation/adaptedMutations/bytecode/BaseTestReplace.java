package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.softevo.util.collections.ArrayList;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ASTParseResult;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.AdaptedMutationDescription;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ParseResultAnalyzer;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.SourceScanner;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace.ReplaceClassAdapter;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace.ReplaceCollectorTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class BaseTestReplace extends BaseTestAdapted {
	//
	// protected static final String TEMPLATE_STRING = "TEMPLATE";
	// protected final String simpleClassName;
	// protected final String packageName;
	// protected final String className;
	// protected final String templateFileName;
	// protected final String testClassName;
	// protected static String[] testCaseNames;

	public BaseTestReplace(Class<?> c) {
		super(c);
		// // Class c = ReplaceTEMPLATE.class;
		// String name = c.getName();
		// className = name.replace(TEMPLATE_STRING, "");
		// simpleClassName = className.substring(className.lastIndexOf('.') +
		// 1);
		// packageName = c.getPackage().getName();
		// templateFileName = "src/test/java/" + className.replace('.', '/')
		// + TEMPLATE_STRING + ".java";
		// testClassName = className + "Test";
		// testCaseNames =
		// ByteCodeTestUtils.generateTestCaseNames(testClassName,
		// 5);

	}

	// private static final int[] linenumbers = { 6, 10, 14, 19, 24 };
	//
	// private static final File OUT_DIR = new File("target/tmp/");
	//
	// private MutationObserver mutationObserver;

	// @Before
	// public void setup() {
	// mutationObserver = new MutationObserver();
	// ByteCodeTestUtils.generateCoverageData(className, testCaseNames,
	// linenumbers);
	// ByteCodeTestUtils.deleteTestMutationResult(className);
	// }
	//
	// @After
	// public void tearDown() {
	// ByteCodeTestUtils.deleteTestMutationResult(className);
	// ByteCodeTestUtils.deleteCoverageData(className);
	// }

	public Class<?> prepareTest() throws Exception {
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		List<Mutation> pos = scan(classFile);
		analyze(outFile);
		redefineMutations(className);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		return clazz;
	}

	// protected void check(int lineNumber, int input, int expectedOutput,
	// int expectedMutations, Method method,
	// List<Mutation> mutationsForClass, Class clazz) throws Exception {
	//
	// int number = 0;
	// for (Mutation m : mutationsForClass) {
	// if (m.getLineNumber() == lineNumber) {
	// System.setProperty(m.getMutationVariable(), "true");
	// System.out.println("TestSkipElse.checkMutation() TESTING "
	// + m.getId());
	// mutationObserver.mutationStart(m);
	// Object instance = clazz.newInstance();
	// Object invoke = method.invoke(instance, input);
	// mutationObserver.mutationEnd(m);
	// System.clearProperty(m.getMutationVariable());
	// assertEquals(expectedOutput, invoke);
	// number++;
	// }
	// }
	// assertEquals("Expected different number of mutations for line "
	// + lineNumber, expectedMutations, number);
	// }
	//
	// private void checkMutation(Method method, MutationType type, int
	// expected)
	// throws IllegalAccessException, InvocationTargetException {
	//
	// Mutation qm = new Mutation(className, "m1(I)I", 7, 0, type);
	// Mutation m = QueryManager.getMutation(qm);
	// System.setProperty(m.getMutationVariable(), "true");
	//
	// System.out.println("TestSkipElse.checkMutation() TESTING " + m.getId());
	// mutationObserver.mutationStart(m);
	// Object invoke = method.invoke(null, 1);
	// mutationObserver.mutationEnd(m);
	// System.clearProperty(m.getMutationVariable());
	// assertEquals(expected, invoke);
	// }

	private void analyze(File outFile) {
		Map<String, ASTParseResult> analyzeJavaFile = SourceScanner
				.analyzeJavaFile(outFile);
		// List<AdaptedMutationDescription> analyze = ParseResultAnalyzer
		// .analyze(analyzeJavaFile);
		List<AdaptedMutationDescription> analyzeReplace = ParseResultAnalyzer
				.analyzeReplace(analyzeJavaFile);
		ParseResultAnalyzer.writeMutations(analyzeReplace);

		// assertEquals(1, analyzeJavaFile.size());
		// ASTParseResult astResult =
		// analyzeJavaFile.values().iterator().next();
		// List<MethodInfo> methodInfos = astResult.getMethodInfos();
		// assertEquals(3, methodInfos.size());
		// List<MethodCallInfo> methodCallInfos =
		// astResult.getMethodCallInfos();
		// assertEquals(2, methodCallInfos.size());

	}

	//
	// private Class<?> loadClass(File outDir) throws MalformedURLException,
	// ClassNotFoundException {
	// URLClassLoader loader = new URLClassLoader(new URL[] { OUT_DIR.toURI()
	// .toURL() });
	// Class<?> clazz = loader.loadClass(className);
	// return clazz;
	// }

	private void transform(File classFile) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(classFile);
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		ReplaceClassAdapter cv = new ReplaceClassAdapter(cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] result = cw.toByteArray();
		String transformed = AsmUtil.checkClass(result);
		// String transformed = AsmUtil.classToString(result);
		FileUtils.writeByteArrayToFile(classFile, result);
		System.out.println("---------------------------------------");
		System.out.println(transformed);
	}

	private List<Mutation> scan(File classFile) {
		MutationPossibilityCollector mpc = generateTestDataInDB(classFile
				.getAbsolutePath(), new ReplaceCollectorTransformer(null));
		System.setProperty("mutation.run.mode", "mutation");
		System.setProperty("invariant.mode", "off");
		redefineMutations(className);
		List<Mutation> pos = mpc.getPossibilities();
		return pos;
	}

	// private File createTmpJavaFile(String filename, File outDir)
	// throws IOException {
	// File file = new File(filename);
	// List<String> lines = FileUtils.readLines(file);
	// List<String> writeLines = new ArrayList<String>();
	// for (String l : lines) {
	// if (l.contains(TEMPLATE_STRING)) {
	// l = l.replace(TEMPLATE_STRING, "");
	// }
	// writeLines.add(l);
	// }
	// if (outDir.exists()) {
	// FileUtils.deleteDirectory(outDir);
	// }
	// outDir.mkdirs();
	// File outFile = new File(outDir, simpleClassName + ".java");
	// FileUtils.writeLines(outFile, writeLines);
	// return outFile;
	// }
	//
	// private void compileTest(File outFile) {
	// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// ByteArrayOutputStream err = new ByteArrayOutputStream();
	// int result = compiler.run(null, out, err, outFile.getAbsolutePath());
	// assertEquals("Compiler failed:\nOut:" + out.toString() + "\nErr:"
	// + err.toString(), 0, result);
	//
	// }
	//
	// protected void checkUnmutated(int input, int expectedOutput, Method
	// method,
	// Class<?> clazz) throws Exception {
	// Object instance = clazz.newInstance();
	// Object invoke = method.invoke(instance, input);
	// assertEquals("Expected different result for unmutated run of method "
	// + method + " with input " + input + ".", expectedOutput, invoke);
	// }

}
