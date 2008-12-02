package de.unisb.cs.st.javalanche.rhino;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.MakefileGenerator;
import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.MakefileGenerator.Target;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;

public final class RhinoTestDriver extends MutationTestDriver {

	private static final String TEST_FILE = "rhino.test.file";

	private static final String TEST_BASE_PATH = System
			.getProperty("VERSION_DIRECTORY")
			+ "/"
			+ System.getProperty("fixId")
			+ "/"
			+ System.getProperty("tag") + "/mozilla/js/tests/";

	private static Logger logger = Logger.getLogger(RhinoTestDriver.class);

	private static boolean dirMode = true;

	private static boolean makeFileMode = false;

	private String baseDir = TEST_BASE_PATH;

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		if (System.getProperty(SINGLE_TEST_NAME_KEY) != null) {
			new RhinoTestDriver().runSingleTest();
		} else {
			if (dirMode) {
				String dirName = System.getProperty("rhino.dir");

				if (dirName != null) {
					File directory = new File(dirName);
					new RhinoTestDriver().testDirectory(directory);
				}
			} else if (makeFileMode) {
				String dirName = System.getProperty("rhino.dir");

				if (dirName != null) {
					File directory = new File(dirName);
					new RhinoTestDriver().generateMakefile(directory);
				}

			} else {
				new RhinoTestDriver().run();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<String> getAllTests() {
		String rhinoTestFile = Util.getPropertyOrFail(TEST_FILE);
		try {
			List<String> allTests = FileUtils
					.readLines(new File(rhinoTestFile));
			return allTests;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected MutationTestRunnable getTestRunnable(String testName) {
		File script = new File(baseDir, testName);
		File parentFile = script.getParentFile().getParentFile();
		File shellFile = new File(parentFile, "shell.js");
		File optionalShellFile = new File(script.getParentFile(), "shell.js");
		RhinoTestRunnable r = new RhinoTestRunnable(shellFile,
				optionalShellFile, script);
		return r;
	}

	private void testDirectory(File dir) {
		baseDir = dir.getAbsolutePath();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		File[] files = getTestFilesForDir(dir);
		List<String> passingTests = new ArrayList<String>();
		List<SingleTestResult> failingTests = new ArrayList<SingleTestResult>();
		logger.info("Searching in " + dir);
		List<String> failingList = Io.getLinesFromFile(new File("failing.txt"));
		List<String> ignoreList = Io.getLinesFromFile(new File(
				"output/271545/post-fix/mozilla/js/tests/rhino-n.tests"));

		// int dbg = 0;
		int ignore = 0;
		for (File f : files) {
			logger.info("File:  " + f);
			String testName = getRelativeLocation(f);
			logger.info("Test name: " + testName);
			// if (!testList.contains(testName)) {
			// logger.info("Not runnning test. (Not specified in test.txt");
			// continue;
			// }
			// if (dbg++ > 5)
			// break;
			if (ignoreList.contains(testName)) {
				logger
						.info("Not running test because its on the projects ignore list");
				ignore++;
				continue;
			}
			if (failingList.contains(testName)) {
				String message = "Not running test (Failed in previous run)";
				logger.info(message + testName);
				failingTests.add(new SingleTestResult(testName, message, false,
						0));
				continue;
			}
			MutationTestRunnable testRunnable = getTestRunnable(testName);
			runWithTimeout(testRunnable);
			SingleTestResult result = testRunnable.getResult();
			if (result.hasPassed()) {
				logger.info("Test passed: " + testName);
				passingTests.add(testName);
			} else {
				logger.info("Test failed: " + testName);
				logger.info("Reason: " + result);
				failingTests.add(result);
			}

		}

		stopWatch.stop();
		long time = stopWatch.getTime();
		logger.info("Test Suite took "
				+ DurationFormatUtils.formatDurationHMS(time));
		StringBuilder sbPass = new StringBuilder();
		StringBuilder sbFail = new StringBuilder();
		for (String passingTest : passingTests) {
			sbPass.append(passingTest + "\n");
		}
		for (SingleTestResult failingTestResult : failingTests) {
			sbFail.append(failingTestResult.getTestMessage().getTestCaseName()
			/* + "," + failingTestResult.getTestMessage().getMessage() */
			+ "\n");
		}
		Io.writeFile(sbPass.toString(), new File("passingTests.txt"));
		Io.writeFile(sbFail.toString(), new File("failingTests.txt"));
		logger.info("Ignored " + ignore + " tests");
	}

	private void generateMakefile(File dir) {
		baseDir = dir.getAbsolutePath();
		File[] files = getTestFilesForDir(dir);
		logger.info("Searching in " + dir);
		List<String> failingList = Io.getLinesFromFile(new File("failing.txt"));
		List<String> ignoreList = Io.getLinesFromFile(new File(
				"output/271545/post-fix/mozilla/js/tests/rhino-n.tests"));
		int ignore = 0;
		int counter = 0;
		List<Target> targets = new ArrayList<Target>();
		for (File f : files) {
			logger.info("File:  " + f);
			String testName = getRelativeLocation(f);
			logger.info("Test name: " + testName);

			if (ignoreList.contains(testName)) {
				logger
						.info("Not running test because its on the projects ignore list");
				ignore++;
				continue;
			}
			if (failingList.contains(testName)) {
				String message = "Not running test (Failed in previous run)";
				logger.info(message + testName);
				continue;
			}
			RhinoTestRunnable testRunnable = (RhinoTestRunnable) getTestRunnable(testName);
			String command = testRunnable.getCommand();
			System.out.println(command);
			String sig = System.getProperty("fixId") + "-"
					+ System.getProperty("tag");
			targets.add(new Target("task-" + counter, "./runTest.sh "
					+ testName + "  " + sig + " \"" + command + "\""));
			counter++;
		}
		String content = MakefileGenerator.generateMakefile(targets);
		Io.writeFile(content, new File("Makefile"));
	}

	private String getRelativeLocation(File f) {
		String absolute = f.getAbsolutePath();
		String[] split = absolute.split("/");
		int l = split.length;
		return split[l - 3] + "/" + split[l - 2] + "/" + split[l - 1];
	}

	private static File[] getTestFilesForDir(File dir) {
		List<File> filesList = new ArrayList<File>();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				File[] dirs = files[i].listFiles();
				for (int j = 0; j < dirs.length; j++) {
					if (dirs[j].isDirectory()) {
						File[] subFiles = dirs[j]
								.listFiles(new FilenameFilter() {

									public boolean accept(File dir, String name) {
										return name.endsWith(".js");
									}

								});
						filesList.addAll(Arrays.asList(subFiles));
					}
				}
			}
		}
		return filesList.toArray(new File[0]);

	}
}
