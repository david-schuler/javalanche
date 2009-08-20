package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationScanner implements ClassFileTransformer {

	private static final Object JUNIT4_TEST_ADAPTER = "junit.framework.JUnit4TestAdapter";

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = MutationDecisionFactory.SCAN_DECISION;

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
		MutationProperties.checkProperty(MutationProperties.TEST_SUITE_KEY);
		logger.info("Name of test suite: " + MutationProperties.TEST_SUITE);
	}

	public MutationScanner() {
		addShutDownHook();
	}

	public static void addShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		final long mutationPossibilitiesPre = QueryManager
				.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
		final long numberOfTestsPre = QueryManager.getNumberOfTests();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				String message1 = String.format(
						"Got %d mutation possibilities before run.",
						mutationPossibilitiesPre);
				final long mutationPossibilitiesPost = QueryManager
						.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
				String message2 = String.format(
						"Got %d mutation possibilities after run.",
						mutationPossibilitiesPost);
				String message3 = String.format(
						"Added %d mutation possibilities.",
						mutationPossibilitiesPost - mutationPossibilitiesPre);
				long numberOfTests = QueryManager.getNumberOfTestsForProject();
				long addedTests = QueryManager.getNumberOfTests()
						- numberOfTestsPre;
				String testMessage = String
						.format(
								"Added %d tests. Total number of tests for project %s : %d",
								addedTests, MutationProperties.PROJECT_PREFIX,
								numberOfTests);
				long coveredMutations = MutationCoverageFile
						.getNumberOfCoveredMutations();
				String coveredMessage = String
						.format(
								"%d (%.2f %%) mutations are covered by tests.",
								coveredMutations,
								(((double) coveredMutations) / mutationPossibilitiesPost) * 100.);
				System.out.println(message1);
				System.out.println(message2);
				System.out.println(message3);
				System.out.println(testMessage);
				System.out.println(coveredMessage);
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {

			try {

				String classNameWithDots = className.replace('/', '.');
				logger.debug(classNameWithDots);

				if (md.shouldBeHandled(classNameWithDots)) {
					classfileBuffer = mutationScannerTransformer
							.transformBytecode(classfileBuffer);
					logger.info(mpc.size()
							+ " mutation possibilities found for class "
							+ className);

					mpc.updateDB();
					mpc.clear();
				} else {
					logger.debug("Skipping class " + className);
				}
				if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
					BytecodeTasks.integrateTestSuite(classfileBuffer,
							classNameWithDots);
				}

			} catch (Throwable t) {
				t.printStackTrace();
				String message = "Exception during instrumentation";
				logger.warn(message, t);
				logger.warn(Util.getStackTraceString());
				System.out.println(message + " - exiting");
				System.exit(1);
			}
		}
		return classfileBuffer;
	}

}
