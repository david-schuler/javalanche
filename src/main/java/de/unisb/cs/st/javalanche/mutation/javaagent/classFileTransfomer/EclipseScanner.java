package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class EclipseScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(EclipseScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		Set<String> classes = getClasses();

		public boolean shouldBeHandled(String classNameWithDots) {
			return classes.contains(classNameWithDots);
		}

		private Set<String> getClasses() {
			Set<String> classSet = new HashSet<String>();
			String classesProperty = System
					.getProperty(MutationProperties.CLASSES_TO_MUTATE_KEY);
			logger.warn("Looking for these classes " + classesProperty);
			if (classesProperty == null) {
				String message = "No files to scan are specified. Property "
						+ MutationProperties.CLASSES_TO_MUTATE_KEY
						+ " not set ";
				throw new RuntimeException(message);
			}
			classesProperty = classesProperty.replace("\"", "");
			String[] split = classesProperty.split(":");
			for (String st : split) {
				classSet.add(st);
			}
			System.out.println("Looking for these classes " + classSet);
			return classSet;
		}

	};

	public EclipseScanner() {
		MutationScanner.addShutDownHook();
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {

			try {

				String classNameWithDots = className.replace('/', '.');
				logger.debug(classNameWithDots);

				if (md.shouldBeHandled(classNameWithDots)) {
					if (!isTest(classfileBuffer)) {
						classfileBuffer = mutationScannerTransformer
								.transformBytecode(classfileBuffer);
						logger.warn(mpc.size()
								+ " mutation possibilities found for class "
								+ className);

						mpc.updateDB();
						mpc.clear();
					}
				} else {
					logger.debug("Skipping class " + className);
				}
				if (MutationScanner.compareWithSuiteProperty(classNameWithDots)) {
					logger.warn("Trying to integrate ScanAndCoverageTestSuite "
							+ classNameWithDots);
					BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer
							.getIntegrateScanAndCoverageTestSuiteTransformer();
					classfileBuffer = integrateSuiteTransformer
							.transformBytecode(classfileBuffer);
					// logger.debug(AsmUtil.classToString(classfileBuffer));
				}

			} catch (Throwable t) {
				t.printStackTrace();
				String message = "Exception during instrumentation";
				logger.warn(message, t);
				logger.warn(Util.getStackTraceString());
				System.out.println(message + " - exiting");
				System.exit(1);
			}
		}
		return classfileBuffer;
	}

	private boolean isTest(byte[] bytecode) {
		ClassReader cr = new ClassReader(bytecode);
		ClassWriter cw = new ClassWriter(0);
		IsTestVisitor cv = new IsTestVisitor(cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		return cv.isTest();
	}

}
