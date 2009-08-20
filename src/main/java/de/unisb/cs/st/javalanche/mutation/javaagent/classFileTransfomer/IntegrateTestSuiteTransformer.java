package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;

public class IntegrateTestSuiteTransformer implements
		ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(IntegrateTestSuiteTransformer.class);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		try {
			if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
				classfileBuffer = BytecodeTasks.integrateTestSuite(
						classfileBuffer, classNameWithDots);
			}
		} catch (Throwable t) {
			logger.warn(t.getMessage());
			t.printStackTrace();
			throw new RuntimeException(t);
		}
		return classfileBuffer;
	}

}
