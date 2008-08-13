 package org.softevo.mutation.javaagent;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.softevo.mutation.javaagent.classFileTransfomer.IntegrateCheckNamesSuiteTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.IntegrateRandomPermutationTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.MutationScanner;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

import de.unisb.cs.st.invariants.javaagent.DaikonInvariantTransformer;
import de.unisb.cs.st.invariants.javaagent.InvariantTransformer;

import static org.softevo.mutation.properties.MutationProperties.*;
import static org.softevo.mutation.properties.MutationProperties.RunMode.*;

public class MutationPreMain {

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		Mutation someMutation = new Mutation("SomeMutationToAddToTheDb", 23,
				23, MutationType.ARITHMETIC_REPLACE,false);
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
				System.out.println("Run mutation tests with invariant checks");
//				addClassFileTransformer(instrumentation, new InvariantTransformer());
				addClassFileTransformer(instrumentation, new DaikonInvariantTransformer());
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;
			} else if (RUN_MODE == MUTATION_TEST_NO_INVARIANT) {
				System.out.println("Run mutation tests without invariant checks");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;
			} else if (RUN_MODE == SCAN) {
				System.out.println("Scanning for mutations");
				addClassFileTransformer(instrumentation, new MutationScanner());
				return;
			} else if (RUN_MODE == TEST_TESTSUIT_FIRST) {
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void addClassFileTransformer(
			Instrumentation instrumentation, ClassFileTransformer clt) {
		classFileTransformer = clt;
		instrumentation.addTransformer(classFileTransformer);
	}

}
