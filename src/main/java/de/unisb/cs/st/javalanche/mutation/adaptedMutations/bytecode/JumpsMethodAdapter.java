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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class JumpsMethodAdapter extends AbstractJumpsAdapter {

	private static Logger logger = Logger.getLogger(JumpsMethodAdapter.class);

	private final MutationManager mutationManager;

	private Multimap<Integer, Label> labelMap = new HashMultimap<Integer, Label>();

	private int lastLine;

	private Label elseEndLabel;

	private int elseLine;

	private BiMap<Label, Label> gotoLabelMap = new HashBiMap<Label, Label>();

	private Label lastLabel;

	private Stack<Mutation> alwaysElseMutations = new Stack<Mutation>();

	public JumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected void handleMutation(Mutation mutation, final Label label,
			final int opcode) {
		List<Mutation> mutations = QueryManager
				.getAdaptedJumpMutations(mutation);
		boolean shouldApply = false;
		for (Mutation m : mutations) {
			if (mutationManager.shouldApplyMutation(m)) {
				shouldApply = true;
				break;
			}
		}
		if (shouldApply) {
			logger.debug("Applying mutation for line: " + getLineNumber());

			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}

			};

			List<MutationCode> mutationCode = new ArrayList<MutationCode>();

			for (final Mutation m2 : mutations) {
				MutationCode mutated = new MutationCode(m2) {

					@Override
					public void insertCodeBlock(MethodVisitor mv) {
						if (jumpReplacementMap.containsKey(opcode)) {
							MutationType type = m2.getMutationType();
							if (type == MutationType.ADAPTED_REMOVE_CHECK) {
								mv.visitInsn(Opcodes.POP2);
							} else {
								int insertOpcode = opcode;
								if (type == MutationType.ADAPTED_NEGATE_JUMP_IN_IF) {
									insertOpcode = JumpReplacements
											.getReplacementMap().get(opcode);
								}
								if (type == MutationType.ADAPTED_SKIP_IF) {
									mv.visitInsn(Opcodes.POP2);
									insertOpcode = Opcodes.GOTO;
								}
								if (type == MutationType.ADAPTED_ALWAYS_ELSE) {
									alwaysElseMutations.push(m2);
									elseLine = JumpInfo.getTargetLine(mutation);
								}
								Label targetLabel = getTargetLabel(m2);
								mv.visitJumpInsn(insertOpcode, targetLabel);
							}
						} else {
							throw new RuntimeException(
									"Invalid opcode key for jump Map");
						}
					}

				};
				mutationCode.add(mutated);
			}
			BytecodeTasks.insertIfElse(mv, unMutated, mutationCode
					.toArray(new MutationCode[0]));
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}

	private Label getTargetLabel(Mutation mutation) {
		int targetLine = JumpInfo.getTargetLine(mutation);
		Label l = new Label();
		registerLabel(targetLine, l);
		return l;
	}

	private void registerLabel(int targetLine, Label l) {
		labelMap.put(targetLine, l);
	}

	private int getReplacement(final int opcode, Mutation mutation) {
		return opcode;
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		if (opcode == Opcodes.GOTO && alwaysElseMutations.size() > 0) {
			final Label l = new Label();
			gotoLabelMap.put(label, l);
			lastLabel = l;

			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}
			};
			MutationCode mutated = new MutationCode(alwaysElseMutations.peek()) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, l);

				}
			};
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			super.visitJumpInsn(opcode, label);
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		System.out.println("JumpsMethodAdapter.visitLineNumber()");
		super.visitLineNumber(line, start);
		for (int l = lastLine; l <= line; l++) {
			System.out.println("l " + l + "  " + line);
			if (labelMap.containsKey(l)) {
				Collection<Label> collection = labelMap.get(l);
				System.out.println("Collection " + collection + "  "
						+ collection.size());
				for (Label target : collection) {
					System.out.println("Line:" + line);
					System.out.println(l);
					System.out.println(start.getOffset());
					mv.visitLabel(target);
				}
				System.out.println(labelMap.containsKey(l));
				labelMap.removeAll(l);
				System.out.println(labelMap.containsKey(l));
			}
		}

		if (elseLine > 0 && line > elseLine) {
			if (lastLabel != null) {
				mv.visitLabel(lastLabel);
				Label keyLabel = gotoLabelMap.inverse().get(lastLabel);
				gotoLabelMap.remove(keyLabel);
				alwaysElseMutations.pop();
				lastLabel = null;
			}
		}
		lastLine = line;
	}

	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
		Label gotoLabel = gotoLabelMap.get(label);
		if (gotoLabel != null) {
			mv.visitLabel(gotoLabel);
		}
	}
}
