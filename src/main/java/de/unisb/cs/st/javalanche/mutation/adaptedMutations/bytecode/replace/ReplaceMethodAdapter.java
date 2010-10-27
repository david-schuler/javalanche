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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class ReplaceMethodAdapter extends AbstractReplaceAdapter {

	private static Logger logger = Logger.getLogger(ReplaceMethodAdapter.class);

	private final MutationManager mutationManager;

	private Multimap<Integer, Label> labelMap = HashMultimap.create();

	private int lastLine;

	public ReplaceMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected boolean handleMutation(Mutation mutation, Type type,
			MutationCode unMutated, boolean fieldInsn, boolean store) {
		Mutation replaceMutation = QueryManager.getReplaceMutation(mutation);
		if (replaceMutation != null
				&& mutationManager.shouldApplyMutation(replaceMutation)) {
			logger.debug("Applying mutation for line: " + getLineNumber());
			Mutation dbMutation = replaceMutation;
			MutationCode mc = getMutationCode(dbMutation, type, fieldInsn,
					unMutated, store);
			BytecodeTasks
					.insertIfElse(mv, unMutated, new MutationCode[] { mc });
			return false;
		}
		return true;
	}

	private MutationCode getMutationCode(Mutation mutation, Type type,
			final boolean fieldInsn, final MutationCode unMutated,
			final boolean store) {
		MutationCode mc;
		if (type == INT_TYPE || type == SHORT_TYPE || type == CHAR_TYPE
				|| type == BOOLEAN_TYPE || type == BYTE_TYPE) {
			mc = new MutationCode(mutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (store) {
						mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
						unMutated.insertCodeBlock(mv);
					} else {
						if (fieldInsn)
							mv.visitInsn(POP);
						mv.visitInsn(ICONST_0);
					}
				}
			};
		} else if (type == FLOAT_TYPE) {

			mc = new MutationCode(mutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {

					if (store) {
						mv.visitInsn(POP);
						mv.visitInsn(FCONST_0);
						unMutated.insertCodeBlock(mv);
					} else {
						if (fieldInsn)
							mv.visitInsn(POP);
						mv.visitInsn(FCONST_0);
					}
				}
			};
		} else if (type == DOUBLE_TYPE) {
			mc = new MutationCode(mutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (store) {
						mv.visitInsn(POP2);
						mv.visitInsn(DCONST_0);
						unMutated.insertCodeBlock(mv);
					} else {
						if (fieldInsn)
							mv.visitInsn(POP);
						mv.visitInsn(DCONST_0);
					}
				}
			};
		} else if (type == LONG_TYPE) {
			mc = new MutationCode(mutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (store) {
						mv.visitInsn(POP2);
						mv.visitInsn(LCONST_0);
						unMutated.insertCodeBlock(mv);
					} else {
						if (fieldInsn)
							mv.visitInsn(POP);
						mv.visitInsn(LCONST_0);
					}
				}
			};
		} else {
			assert type.getSort() == OBJECT;
			mc = null;
			mc = new MutationCode(mutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (store) {
						mv.visitInsn(POP);
						mv.visitInsn(ACONST_NULL);
						unMutated.insertCodeBlock(mv);
					} else {
						if (fieldInsn)
							mv.visitInsn(POP);
						mv.visitInsn(ACONST_NULL);
					}

				}
			};
		}
		if (mc == null) {
			throw new RuntimeException("Null for type " + type);
		}
		return mc;
	}

}
