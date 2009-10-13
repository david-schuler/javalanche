/*
 * Copyright (C) 2009 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

/**
 * MutationTransformer is used to apply mutations during runtime via a java
 * agent.
 * 
 * @author David Schuler
 * 
 */
@SuppressWarnings("unchecked")
public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(MutationFileTransformer.class);

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		if (MutationProperties.QUERY_DB_BEFORE_START) {
			Mutation someMutation = new Mutation("SomeMutationToAddToTheDb",
					23, 23, MutationType.ARITHMETIC_REPLACE, false);
			Mutation mutationFromDb = QueryManager
					.getMutationOrNull(someMutation);
			if (mutationFromDb == null) {
				MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
				mpc1.addPossibility(someMutation);
				mpc1.toDB();
			}
		}

	}

	private static MutationTransformer mutationTransformer = new MutationTransformer();

	private static MutationsForRun mm = MutationsForRun.getFromDefaultLocation();

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
			"de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.testclasses.SysExit", };

	private static List<String> systemExitClassList = Arrays
			.asList(systemExitClasses);

	static {
		logger.info("Loading MutationFileTransformer");
	}

	private static MutationDecision mutationDecision = MutationDecisionFactory
			.getStandardMutationDecision(classesToMutate);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader
	 * , java.lang.String, java.lang.Class, java.security.ProtectionDomain,
	 * byte[])
	 */
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {
				String classNameWithDots = className.replace('/', '.');
				// logger.info(className + " is passed to transformer");
				if (mutationDecision.shouldBeHandled(classNameWithDots)) {
					logger.debug("Removing calls to System.exit() from class: "
							+ classNameWithDots);
					classfileBuffer = systemExitTransformer
							.transformBytecode(classfileBuffer);
				}
				if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
					classfileBuffer = BytecodeTasks.integrateTestSuite(
							classfileBuffer, classNameWithDots);
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
					logger.debug("Class transformed: " + classNameWithDots);
					String checkClass = checkClass(transformedBytecode);
					if (checkClass != null && checkClass.length() > 0) {
						logger.warn("Check of class failed: " + checkClass);
					}
					return transformedBytecode;
				}
			} catch (Throwable t) {
				logger.fatal(
						"Transformation of class " + className + " failed", t);
				StringWriter writer = new StringWriter();
				t.printStackTrace(new PrintWriter(writer));
				logger.fatal(writer.getBuffer().toString());
				t.printStackTrace();
				System.exit(0);
				// throw new RuntimeException(e.getMessage());
			}
		}
		return classfileBuffer;
	}

	private String checkClass(byte[] transformedBytecode) {
		ClassReader cr = new ClassReader(transformedBytecode);
		StringWriter sw = new StringWriter();
		CheckClassAdapter check = new CheckClassAdapter(new ClassWriter(
				ClassWriter.COMPUTE_MAXS));
		cr.accept(check, ClassReader.EXPAND_FRAMES);
		// cr.accept(check,0);
		// CheckClassAdapter.verify(cr, false, new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Checks if the given class name equals to the test suite property.
	 * 
	 * @param classNameWithDots
	 *            the class name to check
	 * @return true, if
	 */
	public static boolean compareWithSuiteProperty(String classNameWithDots) {
		String testSuiteName = System
				.getProperty(MutationProperties.TEST_SUITE_KEY);
		return testSuiteName != null
				&& classNameWithDots.contains(testSuiteName);

	}

	/**
	 * Checks if the given class contains a System.exit() call.
	 * 
	 * @param classNameWithDots
	 *            the class to check
	 * @return true, if the class contains a call to System.exit()
	 */
	private boolean isSystemExitClass(String classNameWithDots) {
		return systemExitClassList.contains(classNameWithDots);
	}

}
