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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.JumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.ArithmeticReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.EvolutionArithmeticReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.RemoveMethodCallsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.RicMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class EvolutionMutationTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(EvolutionMutationTransformer.class);

	public static class EvolutionMutationsClassAdapter extends ClassAdapter {

		private String className;

		private Map<Integer, Integer> ricPossibilities = new HashMap<Integer, Integer>();

		private Map<Integer, Integer> arithmeticPossibilities = new HashMap<Integer, Integer>();

		private Map<Integer, Integer> negatePossibilities = new HashMap<Integer, Integer>();

		private Map<Integer, Integer> removeCallsPossibilities = new HashMap<Integer, Integer>();

		private final MutationManager mutationManager;

		public EvolutionMutationsClassAdapter(ClassVisitor cv) {
			this(cv, new MutationManager());

		}

		public EvolutionMutationsClassAdapter(ClassVisitor cv,
				MutationManager mm) {
			super(cv);
			this.mutationManager = mm;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			super
					.visit(version, access, name, signature, superName,
							interfaces);
			className = name;
			logger.info("Transforming class " + className);
		}

		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, final String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			mv = new CheckMethodAdapter(mv);
			// mv = new RicMethodAdapter(mv, className, name, ricPossibilities,
			// mutationManager);
			// mv = new NegateJumpsMethodAdapter(mv, className, name,
			// negatePossibilities, mutationManager);
			mv = new EvolutionArithmeticReplaceMethodAdapter(mv, className,
					name, arithmeticPossibilities, mutationManager, desc);
			// mv = new RemoveMethodCallsMethodAdapter(mv, className, name,
			// removeCallsPossibilities, mutationManager);
			return mv;
		}
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cv = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(
					MutationPreMain.sysout));
		}

		return new EvolutionMutationsClassAdapter(cv);
	}

}
