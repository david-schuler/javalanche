package org.softevo.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		private String prefix = System
				.getProperty(MutationProperties.PROJECT_PREFIX_KEY);

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")) {
				return false;
			}
			if (classNameWithDots.toLowerCase().contains("test")) {
				return false;
			}
			if (prefix != null && classNameWithDots.startsWith(prefix)) {
				if (QueryManager.hasMutationsforClass(classNameWithDots)) {
					return false;
				}
				return true;
			}
			if (classNameWithDots.startsWith("org.aspectj")) {
				if (QueryManager.hasMutationsforClass(classNameWithDots)) {
					return false;
				}
				return true;
			}
			return false;
		}
	};

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
		mpc1.addPossibility(new Mutation("SomeMutationToAddToTheDb", 23, 23,
				MutationType.ARITHMETIC_REPLACE));
		mpc1.toDB();
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			String classNameWithDots = className.replace('/', '.');
			logger.info(classNameWithDots);
			if (md.shouldBeHandled(classNameWithDots)) {
				classfileBuffer = mutationScannerTransformer
						.transformBytecode(classfileBuffer);
				logger.info("Possibilities found for class " + className + " "
						+ mpc.size());
				mpc.updateDB();
				mpc.clear();
			} else {
				logger.info("Skipping class " + className);
			}
			if (classNameWithDots.endsWith("AllTests")
					|| compareWithSuiteProperty(classNameWithDots)) {
				logger.info("Trying to integrate ScanAndCoverageTestSuite");
				BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer
						.getIntegrateScanAndCoverageTestSuiteTransformer();
				classfileBuffer = integrateSuiteTransformer
						.transformBytecode(classfileBuffer);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.info(e.getStackTrace());
		}
		return classfileBuffer;
	}

	public static boolean compareWithSuiteProperty(String classNameWithDots) {
		boolean returnValue = false;
		String testSuiteName = System
				.getProperty(MutationProperties.TEST_SUITE_KEY);
		if (testSuiteName != null && classNameWithDots.contains(testSuiteName)) {
			returnValue = true;
		}
		return returnValue;
	}
}
