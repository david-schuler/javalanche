package org.softevo.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import org.softevo.mutation.properties.MutationProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class IntegrateRandomPermutationTransformer implements
		ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(IntegrateRandomPermutationTransformer.class);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		try {
			if (classNameWithDots.endsWith("AllTests")
					|| compareWithSuiteProperty(classNameWithDots)) {
				logger.info("Trying to integrate RandomPermutationTestSuite");
				BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer.getIntegrateRandomPermutationTestSuiteTransformer();
				classfileBuffer = integrateSuiteTransformer
						.transformBytecode(classfileBuffer);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
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
