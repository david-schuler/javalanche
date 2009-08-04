package de.unisb.cs.st.javalanche.rhino;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;

public class ParseTestOutput {

	private static Logger logger = Logger.getLogger(ParseTestOutput.class);

	private static final String TEST_OUT_FILE_KEY = "test.out.file";

	public static void main(String[] args) {
		String filename = System.getProperty(TEST_OUT_FILE_KEY);
		File f = new File(filename);

		if (f.exists()) {
			logger.info("Parsing file "  + f);
			Map<String, SingleTestResult> parseTestFile = parseTestFile(f);
			StringBuilder pass = new StringBuilder();
			StringBuilder fail = new StringBuilder();
			Set<Entry<String, SingleTestResult>> entrySet = parseTestFile
					.entrySet();
			for (Entry<String, SingleTestResult> entry : entrySet) {
				if (entry.getValue().hasPassed()) {
					logger.info("Test passed: "  + entry.getKey());
					pass.append(entry.getKey()).append('\n');
				} else {
					fail.append(entry.getKey()).append('\n');
				}
			}
			Io.writeFile(pass.toString(), new File("passingTests.txt"));
			Io.writeFile(fail.toString(), new File("failingTests.txt"));
		} else {
			throw new RuntimeException("File does not exist "
					+ f.getAbsolutePath());
		}
	}

	private static Map<String, SingleTestResult> parseTestFile(File f) {
		List<String> linesFromFile = Io.getLinesFromFile(f);
		Map<String, SingleTestResult> resultMap = new HashMap<String, SingleTestResult>();
		String testname = "";
		boolean collectOut = false;
		boolean collectErr = false;
		StringBuilder outBuffer = new StringBuilder();
		StringBuilder errBuffer = new StringBuilder();
		int exitCode = 0;
		for (String line : linesFromFile) {
			if (line.startsWith("RUN TEST START")) {
				testname = "";
				collectOut = false;
				collectErr = false;
				outBuffer = new StringBuilder();
				errBuffer = new StringBuilder();
				exitCode = 0;
			}
			if (line.startsWith("TESTNAME: ")) {
				testname = line.substring("TESTNAME: ".length());
			}
			if (line.startsWith("EXIT CODE: ")) {
				String exitCodeString = line.substring("EXIT CODE: ".length());
				exitCode = Integer.parseInt(exitCodeString);
			}
			if (line.startsWith("ERR")) {
				collectOut = false;
			}
			if (line.startsWith("RUN TEST END")) {
				SingleTestResult result = RhinoTestRunnable
						.getSingleTestResult(testname, outBuffer.toString(),
								errBuffer.toString(), exitCode, 0);
				resultMap.put(testname, result);
			}
			if (collectOut) {
				outBuffer.append(line).append('\n');
			}
			if (collectErr) {
				errBuffer.append(line).append('\n');
			}
			if (line.startsWith("OUT")) {
				collectOut = true;
			}
			if (line.startsWith("ERR")) {
				collectErr = true;
			}

		}
		return resultMap;
	}
}
