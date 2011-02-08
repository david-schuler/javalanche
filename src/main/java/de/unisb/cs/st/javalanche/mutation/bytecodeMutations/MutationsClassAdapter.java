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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.ArithmeticReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.RemoveMethodCallsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.RicMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.ProjectVariables;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.ReplaceVariablesMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

public class MutationsClassAdapter extends ClassAdapter {

	private static final Logger logger = Logger
			.getLogger(MutationsClassAdapter.class);

	private String className;

	private Map<Integer, Integer> ricPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> arithmeticPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> negatePossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> removeCallsPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> jumpsPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> replacePossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> replaceVariablePossibilities = new HashMap<Integer, Integer>();

	private final MutationManager mutationManager;

	private ProjectVariables projectVariables = ProjectVariables.read();

	public MutationsClassAdapter(ClassVisitor cv, MutationManager mm) {
		super(cv);
		this.mutationManager = mm;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		if (version != 50) {
			logger.warn("Got no Java 6 Class. Version: " + version);
		}
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
				negatePossibilities, mutationManager, desc);
		mv = new ArithmeticReplaceMethodAdapter(mv, className, name,
				arithmeticPossibilities, mutationManager, desc);
		mv = new RemoveMethodCallsMethodAdapter(mv, className, name,
				removeCallsPossibilities, mutationManager, desc);
		ReplaceVariablesMethodAdapter rvAdapter = new ReplaceVariablesMethodAdapter(
				mv, className, name, replaceVariablePossibilities, desc,
				mutationManager,
				projectVariables.getStaticVariables(className),
				projectVariables.getClassVariables(className));
		mv = rvAdapter;
		AnalyzerAdapter analyzerAdapter = new AnalyzerAdapter(className,
				access, name, desc, mv);
		rvAdapter.setAnlyzeAdapter(analyzerAdapter);
		mv = analyzerAdapter;
		return mv;

	}

}
