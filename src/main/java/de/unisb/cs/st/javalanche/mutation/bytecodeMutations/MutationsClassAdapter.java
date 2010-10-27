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

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckMethodAdapter;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.JumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace.ReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.ArithmeticReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.RemoveMethodCallsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.RicMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

public class MutationsClassAdapter extends ClassAdapter {

	private String className;

	private Map<Integer, Integer> ricPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> arithmeticPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> negatePossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> removeCallsPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> jumpsPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> replacePossibilities = new HashMap<Integer, Integer>();

	private final MutationManager mutationManager;

	private BytecodeInfo bytecodeInfo;

	public MutationsClassAdapter(ClassVisitor cv, BytecodeInfo lastLineInfo,
			MutationManager mm) {
		this(cv, mm, lastLineInfo);
	}

	public MutationsClassAdapter(ClassVisitor cv, MutationManager mm,
			BytecodeInfo bytecodeInfo) {
		super(cv);
		this.mutationManager = mm;
		this.bytecodeInfo = bytecodeInfo;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		mv = new CheckMethodAdapter(mv);
		mv = new RicMethodAdapter(mv, className, name, ricPossibilities,
				mutationManager, desc);
		mv = new NegateJumpsMethodAdapter(mv, className, name,
				negatePossibilities,
				mutationManager, desc);
		mv = new ArithmeticReplaceMethodAdapter(mv, className, name,
				arithmeticPossibilities, mutationManager, desc);
		mv = new RemoveMethodCallsMethodAdapter(mv, className, name,
				removeCallsPossibilities, mutationManager, desc);
		mv = new JumpsMethodAdapter(mv, className, name, jumpsPossibilities,
				mutationManager, desc, bytecodeInfo);
		mv = new ReplaceMethodAdapter(mv, className, name,
				replacePossibilities, mutationManager, desc);

		return mv;
	}
}
