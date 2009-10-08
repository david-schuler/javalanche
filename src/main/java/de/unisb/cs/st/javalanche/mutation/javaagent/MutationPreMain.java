/*
 * Copyright (C) 2009 Saarland University
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

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
import static de.unisb.cs.st.javalanche.mutation.properties.RunMode.*;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import de.unisb.cs.st.javalanche.coverage.CoverageTransformer;
import de.unisb.cs.st.javalanche.invariants.javaagent.InvariantTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.EclipseScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.IntegrateTestSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanProjectTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Class that is used by the java agent. Depending on the {@link RunMode} it is
 * decided which Transformers are added.
 * 
 * @author David Schuler
 * 
 */
public class MutationPreMain {

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		// Mutation someMutation = new Mutation("SomeMutationToAddToTheDb", 23,
		// 23, MutationType.ARITHMETIC_REPLACE, false);
		// Mutation mutationFromDb =
		// QueryManager.getMutationOrNull(someMutation);
		// if (mutationFromDb == null) {
		// MutationPossibilityCollector mpc1 = new
		// MutationPossibilityCollector();
		// mpc1.addPossibility(someMutation);
		// mpc1.toDB();
		// }

	}

	public static final PrintStream sysout = System.out;

	/**
	 * Decides which transformers to add depending on the {@link RunMode}.
	 * 
	 */
	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		try {
			if (RUN_MODE == MUTATION_TEST) {
				sysout
						.println("Run mutation testing (without impact detection)");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

			} else if (RUN_MODE == MUTATION_TEST_INVARIANT
					|| RUN_MODE == MUTATION_TEST_INVARIANT_PER_TEST
					|| RUN_MODE == CHECK_INVARIANTS_PER_TEST) {
				sysout.println("Run mutation tests with invariant checks");
				if (RUN_MODE == MUTATION_TEST_INVARIANT_PER_TEST
						|| RUN_MODE == CHECK_INVARIANTS_PER_TEST) {
					sysout.println("Doing checks per test");
				}
				addClassFileTransformer(instrumentation,
						new InvariantTransformer());
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

			} else if (RUN_MODE == MUTATION_TEST_COVERAGE) {
				sysout
						.println("Run mutation tests with tracing of coverage data per test");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				addClassFileTransformer(instrumentation,
						new CoverageTransformer());
				return;
			} else if (RUN_MODE == SCAN) {
				sysout.println("Scanning for mutations");
				addClassFileTransformer(instrumentation, new MutationScanner());
				return;
			} else if (RUN_MODE == CHECK_TESTS || RUN_MODE == TEST_PERMUTED) {
				sysout.println("Integrating RandomPermutationTestSuite");
				addClassFileTransformer(instrumentation,
						new IntegrateTestSuiteTransformer());
				return;
			} else if (RUN_MODE == CREATE_COVERAGE) {
				sysout.println("Getting line coverage data for unmutated run.");
				addClassFileTransformer(instrumentation,
						new IntegrateTestSuiteTransformer());
				addClassFileTransformer(instrumentation,
						new CoverageTransformer());
				return;
			} else if (RUN_MODE == SCAN_PROJECT) {
				sysout.println("Scanning project for classes");
				addClassFileTransformer(instrumentation,
						new DistanceTransformer());
				addClassFileTransformer(instrumentation,
						new ScanProjectTransformer());
				return;
			} else if (RUN_MODE == SCAN_ECLIPSE) {
				sysout.println("Scanning project from eclipse");
				addClassFileTransformer(instrumentation, new EclipseScanner());
				return;
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
