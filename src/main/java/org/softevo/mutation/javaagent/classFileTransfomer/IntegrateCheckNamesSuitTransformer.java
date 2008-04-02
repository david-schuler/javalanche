package org.softevo.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;

import static org.softevo.mutation.javaagent.classFileTransfomer.ClassFileTransformerUtil.*;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class IntegrateCheckNamesSuitTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(IntegrateCheckNamesSuitTransformer.class);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		try {
			if (compareWithSuiteProperty(classNameWithDots)) {
				logger.info("Trying to integrate CheckNamesTestSuite");
				BytecodeTransformer integrateCheckNamesTransformer = IntegrateSuiteTransformer
						.getIntegrateCheckNamesTransformer();
				classfileBuffer = integrateCheckNamesTransformer
						.transformBytecode(classfileBuffer);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return classfileBuffer;
	}

}