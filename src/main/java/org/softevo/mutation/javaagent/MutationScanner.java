package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;

public class MutationScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		public boolean shouldBeScanned(String classNameWithDots) {
			return true;
		}
	};

	static {
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
			// if (md.shouldBeScanned(classNameWithDots)) {
			logger.info("scanning class " + className + " " + mpc.size());
			mutationScannerTransformer.transformBytecode(classfileBuffer);
			logger.info("mpc new size" + mpc.size());
			mpc.updateDB();
			mpc.clear();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.info(e.getStackTrace());
		}
		return classfileBuffer;

	}

}
