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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

import static org.objectweb.asm.Opcodes.*;

public abstract class AbstractReplaceVariablesAdapter extends
		AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(AbstractReplaceVariablesAdapter.class);

	private List<VariableInfo> staticVariables = Collections.EMPTY_LIST;
	private List<VariableInfo> classVariables = Collections.EMPTY_LIST;

	private AnalyzerAdapter anlyzeAdapter;

	public AbstractReplaceVariablesAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			String desc, List<VariableInfo> staticVariables,
			List<VariableInfo> classVariables) {
		super(mv, className, methodName, possibilities, desc);
		if (staticVariables != null) {
			this.staticVariables = staticVariables;
		}
		if (classVariables != null) {
			this.classVariables = classVariables;
		}
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
		if (mutationCode) {
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}
		if (opcode == GETSTATIC) {
			if (owner.equals(className)) {
				String[] replaceNames = getStaticReplacePosibilities(name, desc);
				boolean shouldInsert = mutateStatic(opcode, owner, name, desc,
						replaceNames);
				if (shouldInsert) {
					super.visitFieldInsn(opcode, owner, name, desc);
				}
				return;
			}
		}
		if (opcode == GETFIELD) {
			if (owner.equals(className)) {
				String[] replaceNames = getClassReplacePosibilities(name, desc);
				boolean shouldInsert = mutateClassVar(opcode, owner, name,
						desc, replaceNames);
				if (shouldInsert) {
					super.visitFieldInsn(opcode, owner, name, desc);
				}
				return;
			}
		}
		super.visitFieldInsn(opcode, owner, name, desc);

	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		boolean insert = true;
		List<Integer> replaceLocals = null;
		if (opcode == ILOAD) {
			replaceLocals = getLocals(INTEGER, var);
		} else if (opcode == LLOAD) {
			replaceLocals = getLocals(LONG, var);
		} else if (opcode == FLOAD) {
			replaceLocals = getLocals(FLOAT, var);
		} else if (opcode == DLOAD) {
			replaceLocals = getLocals(DOUBLE, var);
		} else if (opcode == ALOAD) {
			List locals = anlyzeAdapter.locals;
			if (locals != null) {
				Object type = locals.get(var);
				replaceLocals = getLocals(type, var);
			}
		}
		if (replaceLocals != null && replaceLocals.size() > 0) {
			insert = mutateLocal(opcode, var, replaceLocals);
		}
		if (insert) {
			super.visitVarInsn(opcode, var);
		}
	}

	private List<Integer> getLocals(Object type, int var) {
		List<Integer> resultPos = new ArrayList<Integer>();
		List locals = anlyzeAdapter.locals;
		if (locals != null) {
			logger.debug(methodName + "   " + locals);
			for (int i = 0; i < locals.size(); i++) {
				Object o = locals.get(i);
				if (i != var && o.equals(type)) {
					resultPos.add(i);
				}
			}
		}
		return resultPos;
	}

	private boolean mutateClassVar(int opcode, String owner, String name,
			String desc, String[] replaceNames) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(), REPLACE_VARIABLE);
		addPossibilityForLine();
		return handleMutation(mutation, opcode, owner, name, desc, replaceNames);
	}

	private boolean mutateStatic(int opcode, String owner, String name,
			String desc, String[] replaceNames) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(), REPLACE_VARIABLE);
		addPossibilityForLine();
		return handleMutation(mutation, opcode, owner, name, desc, replaceNames);
	}

	private boolean mutateLocal(int opcode, int var, List<Integer> replaceLocals) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(), REPLACE_VARIABLE);
		addPossibilityForLine();
		return handleLocalMutation(mutation, opcode, var, replaceLocals);
	}

	private String[] getStaticReplacePosibilities(String name, String desc) {
		List<String> vars = new ArrayList<String>();

		for (VariableInfo vInfo : staticVariables) {
			if (vInfo.getDesc().equals(desc) && !vInfo.getName().equals(name)) {
				vars.add(vInfo.getName());
			}
		}
		return vars.toArray(new String[0]);

	}

	private String[] getClassReplacePosibilities(String name, String desc) {
		List<String> vars = new ArrayList<String>();
		for (VariableInfo vInfo : classVariables) {
			if (vInfo.getDesc().equals(desc) && !vInfo.getName().equals(name)) {
				vars.add(vInfo.getName());
			}
		}
		return vars.toArray(new String[0]);
	}

	// @Override
	// public void visitMethodInsn(final int opcode, final String owner,
	// final String name, final String desc) {
	// if (mutationCode || name.equals("<init>")
	// || (owner.equals("java/lang/System") && name.equals("exit"))) {
	// mv.visitMethodInsn(opcode, owner, name, desc);
	// } else {
	// mutate(opcode, owner, name, desc);
	// }
	//
	// }

	// private void mutate(final int opcode, final String owner,
	// final String name, final String desc) {
	// Mutation mutation = new Mutation(className, getMethodName(),
	// getLineNumber(), getPossibilityForLine(), REMOVE_CALL);
	// logger.debug("Found possibility for line " + getLineNumber());
	// addPossibilityForLine();
	// handleMutation(mutation, opcode, owner, name, desc);
	// }

	protected abstract boolean handleMutation(Mutation mutation, int opcode,
			String owner, String name, String desc, String[] replaceNames);

	protected abstract boolean handleLocalMutation(Mutation mutation,
			int opcode, int var, List<Integer> replaceLocals);

	public void setAnlyzeAdapter(AnalyzerAdapter anlyzeAdapter) {
		this.anlyzeAdapter = anlyzeAdapter;
	}

}
