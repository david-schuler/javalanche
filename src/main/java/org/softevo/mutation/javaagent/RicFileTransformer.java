package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.replaceIntegerConstant.RicTransformer;

public class RicFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(RicFileTransformer.class
			.getName());

	private static Mutations mutations = Mutations.fromXML();

	static {
		logger.info(mutations.toString());
	}

	private static RicTransformer ricTransformer = new RicTransformer(mutations);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		String classNameWithDots = className.replace('/', '.');
		if (mutations.containsClass(classNameWithDots)) {
			logger.info("Transforming: " + classNameWithDots);
			byte[] transformedBytecode = null;
			try {
				transformedBytecode = ricTransformer
						.transformBytecode(classfileBuffer);
			} catch (Exception e) {
				logger.info("Exception thrown" + e);
				e.printStackTrace();
			}
			logger.info("Class transformed: " + classNameWithDots);

			return transformedBytecode;
		}
		// if (className.contains("Advice")) {
		// logger.info("Advice: " + className);
		// }
		return classfileBuffer;
	}

}
