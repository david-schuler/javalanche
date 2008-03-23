package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.softevo.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import org.softevo.mutation.properties.MutationProperties;

public class MutationPreMain {

	public static boolean scanningEnabled;

	private static ClassFileTransformer classFileTransformer = null;

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {
		try {
			String scanForMutations = System
					.getProperty(MutationProperties.SCAN_FOR_MUTATIONS);

			if (scanForMutations != null) {
				if (!scanForMutations.equals("false")) {
					scanningEnabled = true;
					System.out.println("Scanning for mutations");
					addClassFileTransformer(instrumentation,new MutationScanner());
					return;
				}
			}
			String testTestSuite= MutationProperties.TEST_TESTSUITE;
			if(testTestSuite != null){
				System.out.println("Integrating Random Permutation Test Suite");
				addClassFileTransformer(instrumentation,new IntegrateRandomPermutationTransformer());
				return;
			}
			addClassFileTransformer(instrumentation,new MutationFileTransformer());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void addClassFileTransformer(Instrumentation instrumentation, ClassFileTransformer clt) {
		classFileTransformer = clt;
		instrumentation.addTransformer(classFileTransformer);
	}

}
