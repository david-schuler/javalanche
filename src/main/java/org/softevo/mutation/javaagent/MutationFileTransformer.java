package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(MutationFileTransformer.class);

	private static Set<String> mutations;

	static {

		logger.info("class file transformer");
		// Have to get the classes to mutate here. Doing this in the transform
		// method gives strange errors.(deadlock because of checks if hibernate
		// or other core classes should be mutated)
		mutations = QueryManager.getClassNamesToMutate();
	}

	private static String[] testNames = { "org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps",
			"testClass.Arithmetic", "testForOwnClass.ricProject.RicClass" };

	private static MutationTransformer mutationTransformer = new MutationTransformer();

	private static MutationManager mm = MutationManager.getInstance();

	private static Collection<String> classesToMutate = mm.getClassNames();

	private static MutationDecision mutationDecision = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if(classesToMutate.contains(classNameWithDots)){
				return true;
			}
			for (String included : testNames) {
				if (classNameWithDots.endsWith(included)) {
					return true;
				}
			}

			return false;
		}

	};

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		logger.log(Level.DEBUG, "Processing class: " + classNameWithDots
				+ " contained in db: " + mutations.contains(classNameWithDots));
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
