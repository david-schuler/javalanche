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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public abstract class AbstractRemoveCallsAdapter extends
		AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(AbstractRemoveCallsAdapter.class);

	public AbstractRemoveCallsAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		if (mutationCode || name.equals("<init>")
				|| (owner.equals("java/lang/System") && name.equals("exit"))) {
			mv.visitMethodInsn(opcode, owner, name, desc);
		} else {
			mutate(opcode, owner, name, desc);
		}

	}

	private void mutate(final int opcode, final String owner,
			final String name, final String desc) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(), REMOVE_CALL);
		logger.debug("Found possibility for line " + getLineNumber());
		addPossibilityForLine();
		handleMutation(mutation, opcode, owner, name, desc);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode,
			String owner, String name, String desc);

}
