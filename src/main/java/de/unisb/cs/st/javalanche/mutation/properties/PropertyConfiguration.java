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

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.util.MutationUtil;
import static de.unisb.cs.st.javalanche.mutation.properties.PropertyUtil.*;

public class PropertyConfiguration extends JavalancheDefaultConfiguration {

	// TODO store in variables.

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

	public static final String SINGLE_TASK_MODE_KEY = "javalanche.single.task.mode";

	// public static final String TEST_METHODS_KEY = "javalanche.test.methods";
	// public static String TEST_METHODS = getProperty(TEST_METHODS_KEY); TODO

	public static final String STOP_AFTER_FIRST_FAIL_KEY = "javalanche.stop.after.first.fail";

	public static String STORE_EXCEPTION_TRACES_KEY = "javalanche.store.exception.traces";

	public static String STORE_MESSAGES_KEY = "javalanche.store.test.messages";

	/**
	 * The key for the system property that specifies the tests that are used
	 * for mutation testing.
	 */
	public static final String TEST_NAMES_KEY = "javalanche.test.classes";

	static final String TEST_PERMUTATIONS_KEY = "javalanche.test.permutations";

	public static final String USE_THREAD_STOP_KEY = "javalanche.use.thread.stop";

	static {
		logger.info("Loaded log4j configuration from "
				+ MutationUtil.getLog4jPropertiesLocation());
	}

	@Override
	public boolean enableMutationType(MutationType t) {
		String key = "";
		switch (t) {
		case ARITHMETIC_REPLACE:
			key = ENABLE_ARITHMETIC_REPLACE_KEY;
			break;
		case NEGATE_JUMP:
			key = ENABLE_NEGATE_JUMP_KEY;
			break;
		case REMOVE_CALL:
			key = ENABLE_REMOVE_CALL_KEY;

		case REPLACE_CONSTANT:
			key = ENABLE_REPLACE_CONSTANT_KEY;
			break;
		case REPLACE_VARIABLE:
			key = ENABLE_REPLACE_VARIABLE_KEY;
			break;
		default:
			break;
		}
		return getPropertyOrDefault(key, super.enableMutationType(t));
	}

	@Override
	public int getDefaultTimeoutInSeconds() {
		return getPropertyOrDefault(TIMEOUT_IN_SECONDS_KEY,
				super.getDefaultTimeoutInSeconds());
	}

	@Override
	public String getExcludedTests() {
		return getPropertyOrDefault(EXCLUDED_TESTS_KEY,
				super.getExcludedTests());
	}

	@Override
	public String getIgnorePattern() {
		return getPropertyOrDefault(IGNORE_PREFIX_KEY, super.getIgnorePattern());
	}

	@Override
	public String getMutationIdFile() {
		return getPropertyOrDefault(MUTATION_FILE_KEY,
				super.getMutationIdFile());
	}

	@Override
	public File getOutputDir() {
		String property = getProperty(OUTPUT_DIR_KEY);
		if (property != null) {
			return new File(property);
		} else {
			return super.getOutputDir();
		}
	}

	@Override
	public String getProjectPrefix() {
		String projectPrefix = getProperty(PROJECT_PREFIX_KEY);
		if (projectPrefix == null || projectPrefix.length() == 0) {
			logger.warn("No project prefix found (Property: "
					+ PROJECT_PREFIX_KEY + " not set)");
			return super.getProjectPrefix();
		}
		return projectPrefix;
	}

	@Override
	public String getProjectSourceDir() {
		return getPropertyOrDefault(PROJECT_SOURCE_DIR_KEY,
				super.getProjectSourceDir());
	}

	@Override
	public RunMode getRunMode() {
		String runModeString = PropertyUtil.getProperty(RUN_MODE_KEY);
		if (runModeString != null) {
			runModeString = runModeString.toLowerCase();
			for (RunMode runMode : RunMode.values()) {
				if (runMode.getKey().equals(runModeString)) {
					return runMode;
				}
			}
		}
		return super.getRunMode();
	}

	@Override
	public int getSaveInterval() {
		return getPropertyOrDefault(SAVE_INTERVAL_KEY, super.getSaveInterval());
	}

	@Override
	public String getTestNames() {
		return getPropertyOrDefault(TEST_NAMES_KEY, super.getTestNames());
	}

	@Override
	public int getTestPermutations() {
		return getPropertyOrDefault(TEST_PERMUTATIONS_KEY,
				super.getTestPermutations());
	}

	@Override
	public boolean singleTaskMode() {
		return getPropertyOrDefault(SINGLE_TASK_MODE_KEY,
				super.singleTaskMode());
	}

	@Override
	public boolean stopAfterFirstFail() {
		return getPropertyOrDefault(STOP_AFTER_FIRST_FAIL_KEY,
				super.stopAfterFirstFail());
	}

	@Override
	public boolean storeTestMessages() {
		return getPropertyOrDefault(STORE_MESSAGES_KEY,
				super.storeTestMessages());
	}

	@Override
	public boolean storeTraces() {
		return getPropertyOrDefault(STORE_EXCEPTION_TRACES_KEY,
				super.storeTraces());
	}

	@Override
	public boolean useThreadStop() {

		return getPropertyOrDefault(USE_THREAD_STOP_KEY, super.useThreadStop());
	}

}
