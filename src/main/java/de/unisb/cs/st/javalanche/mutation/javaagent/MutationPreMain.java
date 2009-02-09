package de.unisb.cs.st.javalanche.mutation.javaagent;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode.*;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.IntegrateCheckNamesSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.IntegrateRandomPermutationTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.tracer.TraceTransformer;

import de.unisb.cs.st.javalanche.invariants.javaagent.InvariantTransformer;

/**
 * Class that is used by the javaagent.
 *
 * @author David Schuler
 *
 */
public class MutationPreMain {

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		Mutation someMutation = new Mutation("SomeMutationToAddToTheDb", 23,
				23, MutationType.ARITHMETIC_REPLACE, false);
		Mutation mutationFromDb = QueryManager.getMutationOrNull(someMutation);
		if (mutationFromDb == null) {
			MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
			mpc1.addPossibility(someMutation);
			mpc1.toDB();
		}

	}

	public static final PrintStream sysout = System.out;

	private static ClassFileTransformer classFileTransformer = null;

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		try {
			if (RUN_MODE == MUTATION_TEST) {
				System.out
						.println("Run mutation tests (without invariant checks)");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

			} else if (RUN_MODE == MUTATION_TEST_INVARIANT) {
				System.out.println("Run mutation tests with invariant checks");
				// addClassFileTransformer(instrumentation, new
				// InvariantTransformer());
				addClassFileTransformer(instrumentation,
						new InvariantTransformer());
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

			} else if (RUN_MODE == MUTATION_TEST_INVARIANT_PER_TEST
					|| RUN_MODE == CHECK_INVARIANTS_PER_TEST) {
				System.out
						.println("Run mutation tests with invariant checks per test");
				// addClassFileTransformer(instrumentation, new
				// InvariantTransformer());
				addClassFileTransformer(instrumentation,
						new InvariantTransformer());
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;

			} else if (RUN_MODE == MUTATION_TEST_COVERAGE) {
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				addClassFileTransformer(instrumentation, new TraceTransformer());
			} else if (RUN_MODE == SCAN) {
				System.out.println("Scanning for mutations");
				addClassFileTransformer(instrumentation, new MutationScanner());
				return;
			} else if (RUN_MODE == TEST_TESTSUIT_FIRST
					|| RUN_MODE == CREATE_COVERAGE) {
				System.out.println("Integrating RandomPermutationTestSuite");
				addClassFileTransformer(instrumentation,
						new IntegrateRandomPermutationTransformer());
				return;
			} else if (RUN_MODE == TEST_TESTSUITE_SECOND) {
				System.out.println("Check test suite data in db");
				addClassFileTransformer(instrumentation,
						new IntegrateCheckNamesSuiteTransformer());
				return;
			}

		} catch (Throwable t) {
			t.printStackTrace();
			if (true) {
				throw new RuntimeException("Instrumentation failed ", t);
			}
			System.out.println("Exiting now");
			System.exit(5);
		}
	}

	private static void addClassFileTransformer(
			Instrumentation instrumentation, ClassFileTransformer clt) {
		classFileTransformer = clt;
		instrumentation.addTransformer(classFileTransformer);
	}

}
