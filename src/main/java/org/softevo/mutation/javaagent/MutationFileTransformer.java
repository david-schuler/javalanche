package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.ByteCodeTestUtils;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;
import org.softevo.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer;

/**
 * {@link MutationTransformer} is used to apply mutations during runtime via a
 * java agent.
 *
 * @author David Schuler
 *
 */
public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(MutationFileTransformer.class);

	private static String testName = ".testclasses";

	private static MutationTransformer mutationTransformer = new MutationTransformer();

	private static MutationForRun mm = MutationForRun.getInstance();

	private static Collection<String> classesToMutate = mm.getClassNames();

	private static RemoveSystemExitTransformer systemExitTransformer = new RemoveSystemExitTransformer();

	private static final String[] systemExitClasses = new String[] {
			"org.aspectj.tools.ajbrowser.ui.swing.TopFrame",
			"org.aspectj.tools.ajdoc.JavadocRunner$1",
			"org.aspectj.tools.ajdoc.Main",
			"org.aspectj.apache.bcel.classfile.Utility",
			"org.aspectj.apache.bcel.util.CodeHTML",
			"org.aspectj.apache.bcel.verifier.NativeVerifier",
			"org.aspectj.apache.bcel.verifier.TransitiveHull",
			"org.aspectj.apache.bcel.verifier.VerifierAppFrame",
			"org.aspectj.apache.bcel.verifier.VerifyDialog$1",
			"$installer$.org.aspectj.Main",
			"org.aspectj.tools.ajc.Main",
			"RunWeaveTests",
			"WeaveTests",
			"org.aspectj.internal.tools.ant.taskdefs.MainWrapper$1",
			"org.aspectj.internal.tools.ant.taskdefs.MainWrapper",
			"org.aspectj.testing.util.LinkCheck",
			"org.aspectj.testing.server.TestServer",
			"org.aspectj.testing.drivers.Harness",
			"org.softevo.mutation.bytecodeMutations.sysexit.testclasses.SysExit", };

	private static List<String> systemExitClassList = Arrays
			.asList(systemExitClasses);

	private static MutationDecision mutationDecision = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classesToMutate.contains(classNameWithDots)) {
				return true;
			}
			if (classNameWithDots.contains(testName)
					&& !classNameWithDots.endsWith("Test")) {
				//Hack for unittesting
				ByteCodeTestUtils.redefineMutations(classNameWithDots);
				return true;
			}
			return false;
		}

	};

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader,
	 *      java.lang.String, java.lang.Class, java.security.ProtectionDomain,
	 *      byte[])
	 */
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
//		logger.info("Entering transform");
		if (isSystemExitClass(classNameWithDots)) {
			logger.info("Trying to remove calls to system exit from class"
					+ classNameWithDots);
			classfileBuffer = systemExitTransformer
					.transformBytecode(classfileBuffer);
		}
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

	private boolean isSystemExitClass(String classNameWithDots) {
		if (classNameWithDots.toLowerCase().contains("Main")) {
			logger.info("Checking " + classNameWithDots + " for System exit");
		}
		return systemExitClassList.contains(classNameWithDots);
	}

}
