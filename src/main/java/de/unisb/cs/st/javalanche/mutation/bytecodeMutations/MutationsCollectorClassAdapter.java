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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues.AbsoluteValuePossibilitiesAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.PossibilitiesArithmeticReplaceMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsPossibilitiesMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.MyAdviceAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.RemoveCallsPossibilitiesMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.PossibilitiesRicMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.ProjectVariables;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.ReplaceVariablesPossibilitiesMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.VariableInfo;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion.UnaryOperatorPossibilitiesAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

public class MutationsCollectorClassAdapter extends ClassAdapter {

	private String className;

	private boolean debug = true;

	private final MutationPossibilityCollector mpc;

	private Map<Integer, Integer> ricPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> arithmeticPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> negatePossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> removeCallsPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> replaceVariablesPossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> absoluteValuePossibilities = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> unaryOperatorPossibilities = new HashMap<Integer, Integer>();
	// private Map<Integer, Integer> jumpsCallsPossibilities = new
	// HashMap<Integer, Integer>();
	//
	// private Map<Integer, Integer> replaceCallsPossibilities = new
	// HashMap<Integer, Integer>();

	private ProjectVariables projectVariables = ProjectVariables.read();

	public MutationsCollectorClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector mpc) {
		super(cv);
		this.mpc = mpc;
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
		if ((access & Opcodes.ACC_SYNTHETIC) > 0
				|| (access & Opcodes.ACC_BRIDGE) > 0 || name.equals("<clinit>")) {
			return mv;
		}
		if (debug) {
			mv = new CheckMethodAdapter(mv);
		}
		JavalancheConfiguration configuration = ConfigurationLocator
				.getJavalancheConfiguration();
		if (configuration.enableMutationType(REPLACE_CONSTANT)) {
			mv = new PossibilitiesRicMethodAdapter(mv, className, name, mpc,
					ricPossibilities, desc);
		}
		if (configuration.enableMutationType(NEGATE_JUMP)) {
			mv = new NegateJumpsPossibilitiesMethodAdapter(mv, className, name,
					mpc, negatePossibilities, desc);
		}
		if (configuration.enableMutationType(ARITHMETIC_REPLACE)) {
			mv = new PossibilitiesArithmeticReplaceMethodAdapter(mv, className,
					name, mpc, arithmeticPossibilities, desc);
		}
		if (configuration.enableMutationType(REMOVE_CALL)) {
			mv = new RemoveCallsPossibilitiesMethodAdapter(new MyAdviceAdapter(
					mv, access, name, desc), className, name, mpc,
					removeCallsPossibilities, desc);
		}
		if (configuration.enableMutationType(REPLACE_VARIABLE)) {
			ReplaceVariablesPossibilitiesMethodAdapter rvAdapter = new ReplaceVariablesPossibilitiesMethodAdapter(
					mv, className, name, mpc, replaceVariablesPossibilities,
					desc, projectVariables.getStaticVariables(className),
					projectVariables.getClassVariables(className));
			mv = rvAdapter;
			AnalyzerAdapter analyzerAdapter = new AnalyzerAdapter(className,
					access, name, desc, mv);
			rvAdapter.setAnlyzeAdapter(analyzerAdapter);
			mv = analyzerAdapter;
		}
		if (configuration.enableMutationType(ABSOLUT_VALUE)) {
			mv = new AbsoluteValuePossibilitiesAdapter(mv, className, name,
					absoluteValuePossibilities, desc, mpc);
		}
		if (configuration.enableMutationType(UNARY_OPERATOR)) {
			mv = new UnaryOperatorPossibilitiesAdapter(mv, className, name,
					unaryOperatorPossibilities, desc, mpc);
		}
		return mv;
	}

	private List<VariableInfo> staticVariables = new ArrayList<VariableInfo>();

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if ((access & Opcodes.ACC_STATIC) != 0) {
			staticVariables.add(new VariableInfo(name, desc));
		}
		return super.visitField(access, name, desc, signature, value);

	}
}
