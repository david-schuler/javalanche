/*
 * Copyright (C) 2011 Saarland University
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
import java.util.Collection;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

/**
 * MutationTransformer is used to apply mutations during runtime via a java
 * agent.
 * 
 * @author David Schuler
 * 
 */
public class MutationFileTransformer implements ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(MutationFileTransformer.class);


	private final BytecodeTransformer mutationTransformer;

	private static MutationsForRun mm = MutationsForRun
			.getFromDefaultLocation();

	private static Collection<String> classesToMutate = mm.getClassNames();

	private static RemoveSystemExitTransformer systemExitTransformer = new RemoveSystemExitTransformer();


	private static MutationDecision mutationDecision = MutationDecisionFactory
			.getStandardMutationDecision(classesToMutate);

	public MutationFileTransformer(BytecodeTransformer mutationTransformer) {
		this.mutationTransformer = mutationTransformer;
	}

	public MutationFileTransformer() {
		this(new MutationTransformer());
	}

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
					// TODO Remove System exit calls also from additional
					// classes (e.g.libraries)
					logger.debug("Removing calls to System.exit() from class: "
							+ classNameWithDots);
					classfileBuffer = systemExitTransformer
							.transformBytecode(classfileBuffer);
					AsmUtil.checkClass(classfileBuffer);
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
					AsmUtil.checkClass(transformedBytecode);
					return transformedBytecode;
				}
			} catch (Throwable t) {
				logger.fatal(
						"Transformation of class " + className + " failed", t);
				StringWriter writer = new StringWriter();
				t.printStackTrace(new PrintWriter(writer));
				logger.fatal(writer.getBuffer().toString());
				t.printStackTrace();
				throw new RuntimeException(t.getMessage());
			}
		}
		return classfileBuffer;
	}

	// /**
	// * Checks if the given class name equals to the test suite property.
	// *
	// * @param classNameWithDots
	// * the class name to check
	// * @return true, if
	// */
	// public static boolean compareWithSuiteProperty(String classNameWithDots)
	// {
	// String testSuiteName = System
	// .getProperty(MutationProperties.TEST_SUITE_KEY);
	// return testSuiteName != null
	// && classNameWithDots.contains(testSuiteName);
	//
	// }



}
