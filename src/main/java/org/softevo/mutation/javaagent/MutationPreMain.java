package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.softevo.mutation.javaagent.classFileTransfomer.IntegrateCheckNamesSuiteTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.IntegrateRandomPermutationTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import org.softevo.mutation.javaagent.classFileTransfomer.MutationScanner;

import static org.softevo.mutation.properties.MutationProperties.*;
import static org.softevo.mutation.properties.MutationProperties.RunMode.*;

public class MutationPreMain {

	public static boolean scanningEnabled;

	private static ClassFileTransformer classFileTransformer = null;

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		try {
			if (RUN_MODE == MUTAION_TEST) {
				System.out.println("Run mutation tests");
				addClassFileTransformer(instrumentation,
						new MutationFileTransformer());
				return;
			} else if (RUN_MODE == SCAN) {
				scanningEnabled = true;
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
