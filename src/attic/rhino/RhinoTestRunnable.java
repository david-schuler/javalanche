/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.rhino;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.mozilla.javascript.tools.shell.WrappedMain;

import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;

public final class RhinoTestRunnable implements MutationTestRunnable {

	private static Logger logger = Logger.getLogger(RhinoTestRunnable.class);

	private final File shellFile;
	private final File script;
	private final File optionalShellFile;
	private int exitCode;
	private String errString;
	private String outString;
	private boolean hasRun = false;

	private StopWatch stopWatch = new StopWatch();

	RhinoTestRunnable(File shellFile, File optionalShellFile, File script) {
		this.shellFile = shellFile;
		this.optionalShellFile = optionalShellFile;
		this.script = script;
		System.out.println("RhinoTestRunnable.RhinoTestRunnable() for script: "
				+ script);
	}

	public void run() {
		List<String> argList = getArgs();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ByteArrayOutputStream err = new ByteArrayOutputStream();

		String[] arguments = argList.toArray(new String[0]);
		try {
			stopWatch.start();
			exitCode = WrappedMain.wrappedExec(arguments, new PrintStream(out),
					new PrintStream(err));

		} catch (Throwable t) {
			String message = "Caught exception during mutation testing. Exception is most probably caused by the mutation";
			logger.warn(message, t);
			logger
					.warn(t.toString() + " "
							+ Arrays.toString(t.getStackTrace()));
			exitCode = -1;
		} finally {
			stopWatch.stop();
		}
		byte[] outByteArray = out.toByteArray();
		outString = new String(outByteArray);
		byte[] errByteArray = err.toByteArray();
		errString = new String(errByteArray);
		synchronized (this) {
			hasRun = true;
		}
		logger.info("Runnable finsihed  Took "
				+ DurationFormatUtils.formatDurationHMS(stopWatch.getTime())
				+ " \nOUT:\n" + outString + " \nERR\n" + errString);
	}

	private List<String> getArgs() {
		List<String> argList = new ArrayList<String>();
		addFile(argList, shellFile);
		if (optionalShellFile != null && optionalShellFile.exists()) {
			addFile(argList, optionalShellFile);
		}
		addFile(argList, script);
		return argList;
	}

	private static void addFile(List<String> argList, File file) {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException(
					"Expected an existing file but got: " + file);
		}
		argList.add("-f");
		argList.add(file.getAbsolutePath());
	}

	public int getExitCode() {
		return exitCode;
	}

	public String getErr() {
		return errString;
	}

	public String getOut() {
		return outString;
	}

	public boolean hasFinished() {
		return hasRun;
	}

	public SingleTestResult getResult() {
		if (!hasRun) {
			String message = "Cannot produce a result since the test has not finished";
			logger.warn(message);
			throw new RuntimeException(message);
		}
		String outString = getOut();
		String errString = getErr();
		int exitCode = getExitCode();
		long duration = getDuration();
		return getSingleTestResult(getTestsName(script.getAbsolutePath()),
				outString, errString, exitCode, duration);
	}

	private static String getTestsName(String absolutePath) {
		String str = "mozilla/js/tests";
		int start = absolutePath.indexOf(str);
		return absolutePath.substring(start + str.length() + 1);
	}

	public static void main(String[] args) {
		String testsName = getTestsName("/scratch/schuler/subjects/ibugs_rhino-0.1/versionsFailureMatrix/277935/post-fix/mozilla/js/tests/ecma_2/RegExp/properties-002.js");
		System.out.println(testsName);
	}

	public static SingleTestResult getSingleTestResult(String scriptFilename,
			String outString, String errString, int exitCode, long duration) {
		TestOutcome testOutcome = TestOutcome.FAIL;
		if (exitCode != 0 || outString == null || errString == null
				|| errString.length() > 0 || outString.indexOf("FAILED!") > -1
		/* || outString.indexOf("PASSED!") < 0 */) {
			// System.out.println("Test failed " + script);
			if (outString.contains("Exception")
					|| errString.contains("Exception")) {
				testOutcome = TestOutcome.ERROR;
			}
			if (exitCode != 0) {
				if (exitCode != 3) {
					logger
							.warn("Got an error exit exit code different from three: "
									+ exitCode);
					logger.warn("Out:\n" + outString);
					logger.warn("Err:\n" + errString);
				}

			}
		} else {
			testOutcome = TestOutcome.PASS;
		}
		return new SingleTestResult(scriptFilename, errString, testOutcome,
				duration);
	}

	private long getDuration() {
		return stopWatch.getTime();
	}

	public String getCommand() {
		return getCommand(getArgs().toArray(new String[0]));
	}

	public static String getCommand(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("java -cp ");
		String main = "org/mozilla/javascript/tools/shell/Main.class";
		URL resource = ClassLoader.getSystemResource(main);
		if (resource != null) {
			String location = resource.toString().substring(0,
					resource.toString().length() - main.length());
			logger.info(location);
			File f;
			try {
				f = new File(new URI(location));
				sb.append(f.toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		// sb
		// .append(":/scratch/schuler/subjects/ibugs_rhino-0.1/jars/xmlbeans-2.2.0/lib/jsr173_1.0_api.jar:/scratch/schuler/subjects/ibugs_rhino-0.1/jars/xmlbeans-2.2.0/lib/xbean.jar");
		sb.append(":" + System.getProperty("java.class.path"));
		sb.append(" org.mozilla.javascript.tools.shell.Main ");
		for (String arg : args) {
			sb.append(arg + " ");
		}
		String message = sb.toString();
		return message;
	}

	public void setFailed(boolean b) {
		boolean failed = b;
	}

	public void setFailed(String message) {
		// TODO Auto-generated method stub

	}

}
