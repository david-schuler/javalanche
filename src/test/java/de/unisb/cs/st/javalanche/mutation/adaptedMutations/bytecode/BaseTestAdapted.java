package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
import org.softevo.util.collections.ArrayList;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;

public class BaseTestAdapted {

	protected MutationObserver mutationObserver;

	protected static final String TEMPLATE_STRING = "TEMPLATE";
	protected final String simpleClassName;
	protected final String packageName;
	protected final String className;
	protected final String templateFileName;
	protected final String testClassName;
	protected static String[] testCaseNames;

	protected static final File OUT_DIR = new File("target/tmp/");

	private static final int[] linenumbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12 };


	public BaseTestAdapted(Class<?> c) {
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

	protected File createTmpJavaFile(String filename, File outDir)
			throws IOException {
		File file = new File(filename);
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

	protected void checkUnmutated(int input, int expectedOutput, Method method,
			Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Object invoke = method.invoke(instance, input);
		assertEquals("Expected different result for unmutated run of method "
				+ method + " with input " + input + ".", expectedOutput, invoke);
	}

}
