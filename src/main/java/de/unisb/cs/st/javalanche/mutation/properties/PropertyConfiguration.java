/*
 * Copyright (C) 2011 Saarland University
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

package de.unisb.cs.st.javalanche.mutation.properties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import de.unisb.cs.st.javalanche.mutation.util.MutationUtil;
import static de.unisb.cs.st.javalanche.mutation.properties.PropertyUtil.*;

public class PropertyConfiguration extends JavalancheDefaultConfiguration {

	// TODO store read properties in variables.

	/**
	 * The period each test is allowed to run in seconds.
	 */
	private static final String TIMEOUT_IN_SECONDS_KEY = "javalanche.mutation.default.timeout";

	/**
	 * The variables control whether mutation types are enabled
	 */
	public static final String ENABLE_ARITHMETIC_REPLACE_KEY = "javalanche.enable.arithmetic.replace";

	public static final String ENABLE_NEGATE_JUMP_KEY = "javalanche.enable.negate.jump";

	public static final String ENABLE_REMOVE_CALL_KEY = "javalanche.enable.remove.call";

	public static final String ENABLE_REPLACE_CONSTANT_KEY = "javalanche.enable.replace.constant";

	public static final String ENABLE_REPLACE_VARIABLE_KEY = "javalanche.enable.replace.variable";

	/**
	 * Contains fully qualified names of tests that should be excluded (not
	 * executed). Several tests are separated by a colon.
	 */
	public static final String EXCLUDED_TESTS_KEY = "javalanche.excluded.tests";

	/**
	 * A prefix for classes that are ignored.
	 */
	public static final String IGNORE_PREFIX_KEY = "javalanche.ignore.prefix";

	private static final Logger logger = Logger
			.getLogger(PropertyConfiguration.class);

	/**
	 * The name of the mutation task file.
	 */
	public static final String MUTATION_FILE_KEY = "javalanche.mutation.file";

	/**
	 * The name of the directory that is used by javalanche for output.
	 */
	private static final String OUTPUT_DIR_KEY = "javalanche.mutation.output.dir";

	/**
	 * 
	 * The key for the system property that specifies the package prefix of the
	 * project to mutate
	 * 
	 * For example: -Dmutation.package.prefix=org.aspectj
	 */
	public static final String PROJECT_PREFIX_KEY = "javalanche.project.prefix";

	/**
	 * The directory the source files are stored.
	 */
	public static final String PROJECT_SOURCE_DIR_KEY = "javalanche.project.source.dir";

	private static final String RUN_MODE_KEY = "javalanche.run.mode";

	/**
	 * The save interval in which the mutation results are written to the
	 * database.
	 */
	private static final String SAVE_INTERVAL_KEY = "javalanche.save.interval";

	// public static final String TEST_METHODS_KEY = "javalanche.test.methods";
	// public static String TEST_METHODS = getProperty(TEST_METHODS_KEY); TODO

	/**
	 * Controls whether further tests are executed for a mutation when a test
	 * fails.
	 */
	public static final String STOP_AFTER_FIRST_FAIL_KEY = "javalanche.stop.after.first.fail";

	public static String STORE_EXCEPTION_TRACES_KEY = "javalanche.store.exception.traces";

	public static String STORE_MESSAGES_KEY = "javalanche.store.test.messages";

	/**
	 * The key for the system property that specifies the tests that are used
	 * for mutation testing.
	 */
	public static final String TEST_NAMES_KEY = "javalanche.tests";

	static final String TEST_PERMUTATIONS_KEY = "javalanche.test.permutations";

	public static final String USE_THREAD_STOP_KEY = "javalanche.use.thread.stop";

	static {
		logger.info("Loaded log4j configuration from "
				+ MutationUtil.getLog4jPropertiesLocation());
	}

	private boolean timeoutInSecondsCalled;

	private int timeoutInSeconds;

	private boolean excludedTestCalled;

	private String excludedTests;

	private String ignorePattern;

	private boolean ignorePatternCalled;

	private File mutationIdFile;

	private boolean mutationIdFileCalled;

	private boolean outputDirCalled;

	private File outputDir;

	private String projectPrefix;

	private boolean projectPrefixCalled;

	private String projectSourceDir;

	private boolean projectSourceDirCalled;

	private boolean runModeCalled;

	private RunMode runMode;

	private boolean saveIntervalCalled;

	private int saveInterval;

	private boolean testNamesCalled;

	private String testNames;

	private boolean testPermutationsCalled;

	private int testPermutations;

	private boolean stopAfterFirstFail;

	private boolean stopAfterFirstFailCalled;

	private boolean storeTestMessagesCalled;

	private boolean storeTestMessages;

	private boolean storeTracesCalled;

	private boolean storeTraces;

	private boolean useThreadStop;

	private boolean useThreadStopCalled;

	private boolean enableMutationCalled;

	private Map<MutationType, Boolean> mutationTypeMap;

	@Override
	public boolean enableMutationType(MutationType t) {
		if (!enableMutationCalled) {
			enableMutationCalled = true;
			intitMutationTypeMap();
		}
		return mutationTypeMap.get(t);
	}

	private void intitMutationTypeMap() {
		mutationTypeMap = new HashMap<MutationType, Boolean>();
		setValue(ENABLE_ARITHMETIC_REPLACE_KEY, ARITHMETIC_REPLACE);
		setValue(ENABLE_NEGATE_JUMP_KEY, NEGATE_JUMP);
		setValue(ENABLE_REMOVE_CALL_KEY, REMOVE_CALL);
		setValue(ENABLE_REPLACE_CONSTANT_KEY, REPLACE_CONSTANT);
		setValue(ENABLE_REPLACE_VARIABLE_KEY, REPLACE_VARIABLE);
	}

	public void setValue(String key, MutationType type) {
		boolean value = getPropertyOrDefault(key,
				super.enableMutationType(type));
		mutationTypeMap.put(type, value);
	}

	@Override
	public int getTimeoutInSeconds() {
		if (!timeoutInSecondsCalled) {
			timeoutInSecondsCalled = true;
			timeoutInSeconds = getPropertyOrDefault(TIMEOUT_IN_SECONDS_KEY,
					super.getTimeoutInSeconds());
		}
		return timeoutInSeconds;
	}

	@Override
	public String getExcludedTests() {
		if (!excludedTestCalled) {
			excludedTestCalled = true;
			excludedTests = getPropertyOrDefault(EXCLUDED_TESTS_KEY,
					super.getExcludedTests());
		}
		return excludedTests;
	}

	@Override
	public String getIgnorePattern() {
		if (!ignorePatternCalled) {
			ignorePatternCalled = true;
			ignorePattern = getPropertyOrDefault(IGNORE_PREFIX_KEY,
					super.getIgnorePattern());
		}
		return ignorePattern;
	}

	@Override
	public File getMutationIdFile() {
		if (!mutationIdFileCalled) {
			mutationIdFileCalled = true;
			String mutationIdFileName = getProperty(MUTATION_FILE_KEY);
			if (mutationIdFileName != null) {
				mutationIdFile = new File(mutationIdFileName);
			} else {
				mutationIdFile = super.getMutationIdFile();
			}
		}
		return mutationIdFile;
	}

	@Override
	public File getOutputDir() {
		if (!outputDirCalled) {
			outputDirCalled = true;
			String property = getProperty(OUTPUT_DIR_KEY);
			if (property != null) {
				outputDir = new File(property);
			} else {
				outputDir = super.getOutputDir();
			}
		}
		return outputDir;
	}

	@Override
	public String getProjectPrefix() {
		if (!projectPrefixCalled) {
			projectPrefixCalled = true;
			projectPrefix = getProperty(PROJECT_PREFIX_KEY);
			if (projectPrefix == null || projectPrefix.length() == 0) {
				logger.warn("No project prefix found (Property: "
						+ PROJECT_PREFIX_KEY + " not set)");
				projectPrefix = super.getProjectPrefix();
			}
		}
		return projectPrefix;
	}

	@Override
	public String getProjectSourceDir() {
		if (!projectSourceDirCalled) {
			projectSourceDirCalled = true;
			projectSourceDir = getPropertyOrDefault(PROJECT_SOURCE_DIR_KEY,
					super.getProjectSourceDir());
		}
		return projectSourceDir;
	}

	@Override
	public RunMode getRunMode() {
		if (!runModeCalled) {
			runModeCalled = true;
			String runModeString = PropertyUtil.getProperty(RUN_MODE_KEY);
			if (runModeString != null) {
				runModeString = runModeString.toLowerCase();
				for (RunMode rm : RunMode.values()) {
					if (rm.getKey().equals(runModeString)) {
						runMode = rm;
						break;
					}
				}
			}
			if (runMode == null) {
				runMode = super.getRunMode();
			}
		}
		return runMode;
	}

	@Override
	public int getSaveInterval() {
		if (!saveIntervalCalled) {
			saveIntervalCalled = true;
			saveInterval = getPropertyOrDefault(SAVE_INTERVAL_KEY,
					super.getSaveInterval());
		}
		return saveInterval;
	}

	@Override
	public String getTestNames() {
		if (!testNamesCalled) {
			testNamesCalled = true;
			testNames = getPropertyOrDefault(TEST_NAMES_KEY,
					super.getTestNames());
		}
		return testNames;
	}

	@Override
	public int getTestPermutations() {
		if (!testPermutationsCalled) {
			testPermutationsCalled = true;
			testPermutations = getPropertyOrDefault(TEST_PERMUTATIONS_KEY,
					super.getTestPermutations());
		}
		return testPermutations;
	}

	@Override
	public boolean stopAfterFirstFail() {
		if (!stopAfterFirstFailCalled) {
			stopAfterFirstFailCalled = true;
			stopAfterFirstFail = getPropertyOrDefault(
					STOP_AFTER_FIRST_FAIL_KEY, super.stopAfterFirstFail());
		}
		return stopAfterFirstFail;
	}

	@Override
	public boolean storeTestMessages() {
		if (!storeTestMessagesCalled) {
			storeTestMessagesCalled = true;
			storeTestMessages = getPropertyOrDefault(STORE_MESSAGES_KEY,
					super.storeTestMessages());
		}
		return storeTestMessages;
	}

	@Override
	public boolean storeTraces() {
		if (!storeTracesCalled) {
			storeTracesCalled = true;
			storeTraces = getPropertyOrDefault(STORE_EXCEPTION_TRACES_KEY,
					super.storeTraces());
		}
		return storeTraces;
	}

	@Override
	public boolean useThreadStop() {
		if (!useThreadStopCalled) {
			useThreadStopCalled = true;
			useThreadStop = getPropertyOrDefault(USE_THREAD_STOP_KEY,
					super.useThreadStop());
		}
		return useThreadStop;
	}
}
