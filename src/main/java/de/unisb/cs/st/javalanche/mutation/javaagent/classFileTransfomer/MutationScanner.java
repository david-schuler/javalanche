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
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.LastLineClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecisionFactory;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class MutationScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = MutationDecisionFactory.SCAN_DECISION;

	private static BytecodeInfo lastLineInfo = new BytecodeInfo();

	private MutationManager mm = new MutationManager();

	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		Mutation someMutation = new Mutation("SomeMutationToAddToTheDb", "tm",
				23, 23, MutationType.ARITHMETIC_REPLACE);
		Mutation mutationFromDb = QueryManager.getMutationOrNull(someMutation);
		if (mutationFromDb == null) {
			MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
			mpc1.addPossibility(someMutation);
			mpc1.toDB();
		}
		MutationProperties.checkProperty(MutationProperties.TEST_SUITE_KEY);
		logger.info("Name of test suite: " + MutationProperties.TEST_SUITE);
	}

	public MutationScanner() {
		addShutDownHook();
	}

	public static void addShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		final long mutationPossibilitiesPre = QueryManager
				.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
		final long numberOfTestsPre = QueryManager.getNumberOfTests();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				lastLineInfo.write();
				String message1 = String.format(
						"Got %d mutation possibilities before run.",
						mutationPossibilitiesPre);
				final long mutationPossibilitiesPost = QueryManager
						.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
				String message2 = String.format(
						"Got %d mutation possibilities after run.",
						mutationPossibilitiesPost);
				String message3 = String.format(
						"Added %d mutation possibilities.",
						mutationPossibilitiesPost - mutationPossibilitiesPre);
				long numberOfTests = QueryManager.getNumberOfTestsForProject();
				long addedTests = QueryManager.getNumberOfTests()
						- numberOfTestsPre;
				String testMessage = String
						.format(
								"Added %d tests. Total number of tests for project %s : %d",
								addedTests, MutationProperties.PROJECT_PREFIX,
								numberOfTests);
				long coveredMutations = MutationCoverageFile
						.getNumberOfCoveredMutations();
				String coveredMessage = String
						.format(
								"%d (%.2f %%) mutations are covered by tests.",
								coveredMutations,
								(((double) coveredMutations) / mutationPossibilitiesPost) * 100.);
				System.out.println(message1);
				System.out.println(message2);
				System.out.println(message3);
				System.out.println(testMessage);
				System.out.println(coveredMessage);
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {

			try {

				String classNameWithDots = className.replace('/', '.');
				logger.debug(classNameWithDots);
				if (md.shouldBeHandled(classNameWithDots)) {

					computeBytecodeInfo(classfileBuffer);

					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

					ClassVisitor cv = cw;
					// cv = new CheckClassAdapter(cw);
					if (MutationProperties.TRACE_BYTECODE) {
						cv = new TraceClassVisitor(cv, new PrintWriter(
								MutationPreMain.sysout));
					}
					cv = new MutationsCollectorClassAdapter(cv, mpc);
					ClassReader cr = new ClassReader(classfileBuffer);
					cr.accept(cv, ClassReader.EXPAND_FRAMES);
					classfileBuffer = cw.toByteArray();

					AsmUtil.checkClass2(classfileBuffer);
					// classfileBuffer = mutationScannerTransformer
					// .transformBytecode(classfileBuffer);
					logger.info(mpc.size()
							+ " mutation possibilities found for class "
							+ className);

					mpc.updateDB();
					mpc.clear();

				} else {
					logger.debug("Skipping class " + className);
				}

				if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
					classfileBuffer = BytecodeTasks.integrateTestSuite(
							classfileBuffer, classNameWithDots);
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

	private void computeBytecodeInfo(byte[] classfileBuffer) {
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		LastLineClassAdapter cv = new LastLineClassAdapter(cw, lastLineInfo);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
	}

}
