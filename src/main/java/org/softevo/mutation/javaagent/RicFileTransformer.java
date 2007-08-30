package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.replaceIntegerConstant.RicTransformer;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

public class RicFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(RicFileTransformer.class
			.getName());

	 private static Set<String> mutations;

	static {

		logger.info("class file transformer");
		// Have to get the classes to mutate here. Doing this in the transform
		// method gives strange errors.(deadlock because of checks if hibernate
		// or other core classes should be mutated)
		mutations = QueryManager.getClassNamesToMutate();
	}

	private static RicTransformer ricTransformer = new RicTransformer();

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		String classNameWithDots = className.replace('/', '.');
//		logger.info("Processing class: " + classNameWithDots);
		 if (mutations.contains(classNameWithDots)) {
//		if (classNameWithDots.equals(MutationProperties.SAMPLE_FILE_NAME)) {
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
		return classfileBuffer;
	}

}
