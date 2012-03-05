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
package de.unisb.cs.st.javalanche.mutation.javaagent;

import static de.unisb.cs.st.javalanche.mutation.properties.RunMode.*;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import de.unisb.cs.st.javalanche.coverage.CoverageTransformer;
//import de.unisb.cs.st.javalanche.invariants.javaagent.InvariantTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanProjectTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.SysExitTransformer;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;

/**
 * Class that is used by the java agent. Depending on the {@link RunMode} it is
 * decided which Transformers are added.
 * 
 * @author David Schuler
 * 
 * 
 */
public class MutationPreMain {

	public static final PrintStream sysout = System.out;

	/**
	 * Decides which transformers to add depending on the {@link RunMode}.
	 * 
	 */
	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		JavalancheConfiguration javalancheConfiguration = ConfigurationLocator
				.getJavalancheConfiguration();
		RunMode runMode = javalancheConfiguration.getRunMode();
		try {
			if (runMode == MUTATION_TEST) {
				sysout.println("Run mutation testing (without impact detection)");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

				/*
				 * } else if (runMode == MUTATION_TEST_INVARIANT) {
				 * sysout.println("Run mutation tests with invariant checks");
				 * addClassFileTransformer(instrumentation, new
				 * InvariantTransformer());
				 * addClassFileTransformer(instrumentation, new
				 * MutationFileTransformer()); return;
				 */
			} else if (runMode == MUTATION_TEST_COVERAGE) {
				sysout.println("Run mutation tests with tracing of coverage data per test");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				addClassFileTransformer(instrumentation,
						new CoverageTransformer());
				return;
			} else if (runMode == SCAN) {
				sysout.println("Scanning for mutations");
				addClassFileTransformer(instrumentation, new MutationScanner());
				return;
			} else if (runMode == CHECK_TESTS || runMode == TEST_PERMUTED) {
				sysout.println("Running tests in random order.");
				// addClassFileTransformer(instrumentation, new
				// PrintTransformer());
				addClassFileTransformer(instrumentation,
						new SysExitTransformer());
				// addClassFileTransformer(instrumentation,
				// new IntegrateTestSuiteTransformer());
				return;
			} else if (runMode == CREATE_COVERAGE_MULT) {
				sysout.println("Getting line coverage data for multiple unmutated runs.");
				addClassFileTransformer(instrumentation,
						new SysExitTransformer());
				addClassFileTransformer(instrumentation,
						new CoverageTransformer());
				return;
			} else if (runMode == SCAN_PROJECT) {
				sysout.println("Scanning project for classes");
				addClassFileTransformer(instrumentation,
						new DistanceTransformer());
				addClassFileTransformer(instrumentation,
						new ScanVariablesTransformer());
				addClassFileTransformer(instrumentation,
						new ScanProjectTransformer());
				return;
			}else{
				System.err.println("Unexpected run mode: " + runMode);
			}

		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Instrumentation failed", t);
		}
	}

	private static void addClassFileTransformer(
			Instrumentation instrumentation, ClassFileTransformer clt) {
		instrumentation.addTransformer(clt);
	}

}
