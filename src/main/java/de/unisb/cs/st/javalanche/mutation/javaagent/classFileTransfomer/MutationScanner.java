package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationScanner implements ClassFileTransformer {

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

	private void addShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		final long mutationPossibilitiesPre = QueryManager
				.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
		final long numberOfTestsPre = QueryManager.getNumberOfTests();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				String message1 = String.format(
						"Got %d mutation possibilities before run",
						mutationPossibilitiesPre);
				final long mutationPossibilitiesPost = QueryManager
						.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
				String message2 = String.format(
						"Got %d mutation possibilities after run",
						mutationPossibilitiesPost);
				String message3 = String.format(
						"Added %d mutation possibilities.",
						mutationPossibilitiesPost - mutationPossibilitiesPre);
				long numberOfTests = QueryManager.getNumberOfTestsForProject();
				long addedTests = QueryManager.getNumberOfTests()
						- numberOfTestsPre;
				String testMessage = String.format(
						"Added %d tests. Tests for project %s : %d",
						addedTests, MutationProperties.PROJECT_PREFIX,
						numberOfTests);
				long coveredMutations = QueryManager
						.getNumberOfCoveredMutations();
				String coveredMessage = String
						.format(
								"%d mutations are covered by tests which is  %f percent",
								coveredMutations,
								(((double) coveredMutations) / mutationPossibilitiesPost) * 100.);
				logger.info(message1);
				logger.info(message2);
				logger.info(message3);
				logger.info(testMessage);
				logger.info(coveredMessage);
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
					// TraceClassVisitor tr = new TraceClassVisitor(new
					// PrintWriter(MutationPreMain.sysout));
					// ClassReader cr = new ClassReader(classfileBuffer);
					// cr.accept(tr,0);
					// return classfileBuffer;
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
				if (classNameWithDots.endsWith("AllTests")
						|| compareWithSuiteProperty(classNameWithDots)) {
					logger.info("Trying to integrate ScanAndCoverageTestSuite");
					BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer
							.getIntegrateScanAndCoverageTestSuiteTransformer();
					classfileBuffer = integrateSuiteTransformer
							.transformBytecode(classfileBuffer);
					// logger.debug(AsmUtil.classToString(classfileBuffer));
				}

			} catch (Throwable t) {
				t.printStackTrace();
				logger.info(t.getMessage());
				logger.info(Util.getStackTraceString());
				System.out
						.println("Exception during instrumentation - exiting");
				System.exit(1);
			}
		}
		return classfileBuffer;
	}

	public static boolean compareWithSuiteProperty(String classNameWithDots) {
		boolean returnValue = false;
		String testSuiteName = MutationProperties.TEST_SUITE;
		if (testSuiteName != null && classNameWithDots.contains(testSuiteName)) {
			returnValue = true;
		}
		return returnValue;
	}

	public static void main(String[] args) {
		new MutationScanner();
	}
}
