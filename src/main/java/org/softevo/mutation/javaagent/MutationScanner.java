package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")) {
				return false;
			}
			if (QueryManager.hasMutationsforClass(classNameWithDots)) {
				return false;
			}
			if (classNameWithDots.toLowerCase().contains("test")) {
				return false;
			}
			if(classNameWithDots.startsWith("org.aspectj")){
				return true;
			}
			return false;
		}
	};

	static {
		// DB must be loaded before transform method is entered.
		MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
		mpc1.addPossibility(new Mutation("MutationScanner", 23, 23,
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
				mutationScannerTransformer.transformBytecode(classfileBuffer);
				logger.info("Possibilities found for class " + className + " "
						+ mpc.size());
				mpc.updateDB();
				mpc.clear();
			} else {
				logger.info("Skipping class " + className);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.info(e.getStackTrace());
		}
		return classfileBuffer;

	}

}
