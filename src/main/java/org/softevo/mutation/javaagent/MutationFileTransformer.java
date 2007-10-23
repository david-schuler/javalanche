package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;

/**
 * {@link MutationTransformer} is used to apply mutations during runtime via a
 * javaagent.
 *
 * @author David Schuler
 *
 */
public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(MutationFileTransformer.class);


	private static String[] testNames = {
			".testclasses"};

	private static MutationTransformer mutationTransformer = new MutationTransformer();

	private static MutationForRun mm = MutationForRun.getInstance();

	private static Collection<String> classesToMutate = mm.getClassNames();

	private static MutationDecision mutationDecision = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classesToMutate.contains(classNameWithDots)) {
				return true;
			}
			for (String included : testNames) {
				if (classNameWithDots.contains(included)) {
					return true;
				}
			}
			return false;
		}

	};

	/* (non-Javadoc)
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
	 */
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		if (mutationDecision.shouldBeHandled(classNameWithDots)) {
			logger.info("Transforming: " + classNameWithDots);
			byte[] transformedBytecode = null;
			try {
				transformedBytecode = mutationTransformer
						.transformBytecode(classfileBuffer);
			} catch (Exception e) {
				logger.info("Exception thrown: " + e);
				e.printStackTrace();
			}
			logger.info("Class transformed: " + classNameWithDots);
			return transformedBytecode;
		}
		return classfileBuffer;
	}

}
