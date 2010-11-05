/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

/**
 * Class that provides static methods for common bytecode modifications.
 * 
 * @author David Schuler
 * 
 */
public class BytecodeTasks {

	private static Logger logger = Logger.getLogger(BytecodeTasks.class);

	private BytecodeTasks() {
	}

	/**
	 * Inserts a mutation. The inserted code is like this:
	 * <code>if(System.getProperty(mutationID)){
	 * 			execute mutated code
	 * 		}
	 * 		else{
	 * 			execute unmutated code
	 * 		}
	 * 
	 * @param mv
	 *            MethodVisitor where the code is inserted.
	 * @param unMutated
	 *            code that should be used when no mutation is applied.
	 * @param mutations
	 *            code that should be used when one of the mutations is applied.
	 */
	public static void insertIfElse(MethodVisitor mv, MutationCode unMutated,
			MutationCode[] mutations) {
		Label endLabel = new Label();
		Label mutationStartLabel = new Label();
		mutationStartLabel.info = new MutationMarker(true);
		mv.visitLabel(mutationStartLabel);
		for (MutationCode mutationCode : mutations) {
			Mutation mutation = mutationCode.getMutation();
			mv.visitLdcInsn(mutation.getMutationVariable());
			mv.visitLdcInsn(mutation.getMutationType() + "");

			mv.visitInsn(Opcodes.POP);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.IFNULL, l1);

			Label l2 = new Label();
			mv.visitLabel(l2);
			// insertPrintStatements(mv, "Mutation touched: " +
			// mutation.getId());
			insertMutationTouchedCode(mv, mutation);
			if (!MutationProperties.INSERT_ORIGINAL_INSTEAD_OF_MUTATION) {
				mutationCode.insertCodeBlock(mv);
			} else {
				logger.warn("Debug mode: not inserting mutated statement");
				unMutated.insertCodeBlock(mv);
			}
			mv.visitJumpInsn(Opcodes.GOTO, endLabel);
			mv.visitLabel(l1);
		}
		Label mutationEndLabel = new Label();
		mutationEndLabel.info = new MutationMarker(false);
		mv.visitLabel(mutationEndLabel);
		unMutated.insertCodeBlock(mv);
		mv.visitLabel(endLabel);

	}

	/**
	 * Insert calls that signal whether the mutated code was executed.
	 * 
	 * @param mv
	 *            the method visitor to add the statements
	 * @param mutation
	 *            the mutation that is covered or not
	 */
	private static void insertMutationTouchedCode(MethodVisitor mv,
			Mutation mutation) {
		if (MutationProperties.MUTATION_PRINT_STATEMENTS_ENABLED) {
			BytecodeTasks.insertPrintStatements(mv, "Mutation "
					+ mutation.getMutationVariable() + " - "
					+ mutation.getMutationType() + " is enabled");
		}
		mv.visitLdcInsn(mutation.getId());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC,
				"de/unisb/cs/st/javalanche/mutation/runtime/MutationObserver",
				"touch", "(J)V");
	}

	/**
	 * Inserts bytecode that prints the given message.
	 * 
	 * @param mv
	 *            The MethodVisitor for which the code is added.
	 * @param message
	 *            The text to be printed to System.out .
	 */
	public static void insertPrintStatements(MethodVisitor mv, String message) {
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[MUTATION] " + message);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");
	}

	public static byte[] integrateTestSuite(byte[] classfileBuffer,
			String classNameWithDots) {
		if (!shouldIntegrate(classNameWithDots)) {
			throw new IllegalArgumentException("Should not integrate in "
					+ classNameWithDots);
		}
		byte[] result = null;
		if (MutationProperties.JUNIT4_MODE) {
			if (classNameWithDots
					.equals(MutationProperties.JUNIT4_TEST_ADAPTER)) {
				logger
						.info("Integrating in Junit 4 suite "
								+ classNameWithDots);
				result = IntegrateSuiteTransformer
						.modifyJunit4Adapter(classfileBuffer);
			}
		} else {
			logger.info("Integrating in Junit 3 suite " + classNameWithDots);
			BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer
					.getIntegrateTransformer();
			result = integrateSuiteTransformer
					.transformBytecode(classfileBuffer);

		}
		return result;
	}

	public static boolean shouldIntegrate(String classNameWithDots) {
		if (MutationProperties.JUNIT4_MODE) {
			if (classNameWithDots
					.equals(MutationProperties.JUNIT4_TEST_ADAPTER)) {
				return true;
			}
		} else {
			return compareWithSuiteProperty(classNameWithDots);
		}
		return false;
	}

	private static boolean compareWithSuiteProperty(String classNameWithDots) {
		boolean result = false;
		String testSuiteName = MutationProperties.TEST_SUITE;
		if (testSuiteName != null && classNameWithDots.contains(testSuiteName)) {
			result = true;
		}
		return result;
	}
}
