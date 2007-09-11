package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;
import org.softevo.mutation.bytecodeMutations.arithmetic.ArithmeticTransformer;
import org.softevo.mutation.bytecodeMutations.negateJumps.NegateJumpsTransformer;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicTransformer;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationFileTransformer.class);

	private static Set<String> mutations;

	static {

		logger.info("class file transformer");
		// Have to get the classes to mutate here. Doing this in the transform
		// method gives strange errors.(deadlock because of checks if hibernate
		// or other core classes should be mutated)
		mutations = QueryManager.getClassNamesToMutate();
	}

	@SuppressWarnings("unused")
	private static RicTransformer ricTransformer = new RicTransformer();

	private static NegateJumpsTransformer negateJumpsTransformer = new NegateJumpsTransformer();

	private static ArithmeticTransformer arithmeticTransformer = new ArithmeticTransformer();


	@SuppressWarnings("unused")
	private static MutationTransformer mutationTransformer = new MutationTransformer();

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		String classNameWithDots = className.replace('/', '.');
		// logger.info("Processing class: " + classNameWithDots);
		if (mutations.contains(classNameWithDots)) {

			logger.info("Transforming: " + classNameWithDots);

			byte[] transformedBytecode = null;
			try {
//				transformedBytecode = ricTransformer
//						.transformBytecode(classfileBuffer);
				transformedBytecode = arithmeticTransformer.transformBytecode(classfileBuffer);
//				transformedBytecode = mutationTransformer.transformBytecode(classfileBuffer);
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
