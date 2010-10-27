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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

public class JumpsMethodAdapter extends AbstractJumpsAdapter {

	private static Logger logger = Logger.getLogger(JumpsMethodAdapter.class);

	private final MutationManager mutationManager;

	private Multimap<Integer, Label> labelMap = HashMultimap.create();

	private int lastLine;

	// private Label elseEndLabel;

	private int elseLine;

	private BiMap<Label, Label> gotoLabelMap = HashBiMap.create();

	private Label lastLabel = null;

	private Mutation alwaysElseMutation;

	private List<Label> visitedLabels = new ArrayList<Label>();

	private final BytecodeInfo lastLineInfo;

	private int elseEnd;
	private int elseStart;

	private Label elseLabel;

	public JumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc,
			BytecodeInfo lastLineInfo) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
		this.lastLineInfo = lastLineInfo;
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
				if (mutationManager.shouldApplyMutation(m2)) {
					final MutationType type = m2.getMutationType();
					// if (type == MutationType.ADAPTED_SKIP_ELSE) {
					int targetLine = JumpInfo.getTargetLine(m2);
					int lastLine = lastLineInfo.getLastLine(className,
							methodName, desc);
					if (targetLine > lastLine) {
						logger
								.info("Skipping mutation would lead to incorrect bytecode."
										+ m2);
						continue;
					}
					// }
					MutationCode mutated = new MutationCode(m2) {

						@Override
						public void insertCodeBlock(MethodVisitor mv) {
							if (jumpReplacementMap.containsKey(opcode)) {

								if (type == MutationType.ADAPTED_REMOVE_CHECK) {
									mv.visitInsn(getPopOpcode(opcode));
								} else {
									int insertOpcode = opcode;
									if (type == MutationType.ADAPTED_NEGATE_JUMP_IN_IF) {
										insertOpcode = JumpReplacements
												.getReplacementMap()
												.get(opcode);
									}
									if (type == MutationType.ADAPTED_SKIP_IF) {
										mv.visitInsn(getPopOpcode(opcode));
										insertOpcode = Opcodes.GOTO;
									}
									if (type == MutationType.ADAPTED_ALWAYS_ELSE) {
										alwaysElseMutation = m2;
										elseLine = getElseStart(m2);

										// mv.visitLdcInsn(getProperty(m2));
										// mv.visitLdcInsn("TRUE");
										// mv
										// .visitMethodInsn(INVOKESTATIC,
										// "java/lang/System",
										// "setProperty",
										// "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
										// mv.visitInsn(POP);
										// elseStart = getElseStart(m2);
										// elseEnd = getElseEnd(m2);
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
			}
			BytecodeTasks.insertIfElse(mv, unMutated, mutationCode
					.toArray(new MutationCode[0]));
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}

	protected int getElseEnd(Mutation m2) {
		return JumpInfo.getEndLine(m2);
	}

	protected int getElseStart(Mutation m2) {
		return JumpInfo.getTargetLine(m2);
	}

	protected String getProperty(Mutation m2) {
		String res = "ALWAYS_ELSE_" + m2.getId();
		return res;
	}

	private static int getPopOpcode(int opcode) {

		if (opcode == IF_ICMPEQ || opcode == IF_ICMPNE || opcode == IF_ICMPLT
				|| opcode == IF_ICMPGE || opcode == IF_ICMPGT
				|| opcode == IF_ICMPLE || opcode == IF_ACMPEQ
				|| opcode == IF_ACMPNE

		) {
			return POP2;
		}
		return POP;
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
		if (!mutationCode && opcode == Opcodes.GOTO
				&& alwaysElseMutation != null && isForwardLabel(label)) {
			final Label l;
			if (lastLabel != null) {
				l = lastLabel;
				Label checkLabel = gotoLabelMap.get(label);
				if (checkLabel != null && checkLabel.equals(lastLabel)) {

				} else {
					throw new RuntimeException(
							"Last label not null for method: " + className
									+ "." + methodName);
				}
				// l = lastLabel;
			} else {
				// } else {
				l = new Label();
				System.out
						.println("JumpsMethodAdapter.visitJumpInsn() Setting last label "
								+ className + " " + methodName + " ");
				lastLabel = l;
				// }
				gotoLabelMap.put(label, l);
			}
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}
			};
			MutationCode mutated = new MutationCode(alwaysElseMutation) {
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

	private boolean isForwardLabel(Label label) {
		return !visitedLabels.contains(label);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		for (int l = lastLine; l <= line; l++) {
			if (labelMap.containsKey(l)) {
				Collection<Label> collection = labelMap.get(l);
				for (Label target : collection) {
					mv.visitLabel(target);
				}
				labelMap.removeAll(l);
			}
		}

		// if (elseStart > 0 && line > elseStart) {
		// elseStart = -1;
		// elseLabel = new Label();
		// mv.visitLabel(elseLabel);
		// }
		// if (elseEnd > 0 && line > elseEnd) {
		// elseEnd = -1;
		//
		// MutationCode unMutated = new MutationCode(null) {
		// @Override
		// public void insertCodeBlock(MethodVisitor mv) {
		// }
		// };
		// MutationCode mutated = new MutationCode(alwaysElseMutation.peek()) {
		// @Override
		// public void insertCodeBlock(MethodVisitor mv) {
		// mv.visitLdcInsn(getProperty(mutation));
		// mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
		// "getProperty",
		// "(Ljava/lang/String;)Ljava/lang/String;");
		// Label l2 = new Label();
		// mv.visitJumpInsn(IFNULL, l2);
		// mv.visitLdcInsn(getProperty(mutation));
		// mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
		// "clearProperty",
		// "(Ljava/lang/String;)Ljava/lang/String;");
		// mv.visitInsn(POP);
		// mv.visitJumpInsn(GOTO, elseLabel);
		// }
		// };
		// BytecodeTasks.insertIfElse(mv, unMutated,
		// new MutationCode[] { mutated });
		// } else {
		// }
		if (elseLine > 0 && line >= elseLine) {
			if (lastLabel != null) {
				if (!visitedLabels.contains(lastLabel)) {
					System.out.println("LAST " + lastLabel);
					System.out.println("LAST " + lastLabel.getClass());
					mv.visitLabel(lastLabel);
					Label keyLabel = gotoLabelMap.inverse().get(lastLabel);
					gotoLabelMap.remove(keyLabel);
					alwaysElseMutation = null;
					elseLine = -1;
				}
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
			visitedLabels.add(gotoLabel);
			gotoLabelMap.remove(label);
		}
		// System.out.println("LABEL " + label);
		visitedLabels.add(label);
	}

	@Override
	public void visitEnd() {
		if (labelMap.size() > 0) {
			String message = "Not visited labels for lines in method + "
					+ className + "." + methodName + " : " + labelMap.keySet()
					+ "\n" + labelMap;
			logger.warn(message);
			throw new RuntimeException(message);
		}
		// if (lastLabel != null) {
		// String message = "Last Label not visited for method + " + className
		// + "." + methodName + " : " + lastLabel
		// + "\n Visited labels:" + visitedLabels;
		// logger.warn(message);
		// throw new RuntimeException(message);
		// }
		super.visitEnd();
	}

	@Override
	public void visitCode() {

		super.visitCode();
		if (!methodName.contains("init")) {
			Collection<VariableInfo> localVars = lastLineInfo.getLocalVars(
					className, methodName, desc);
			for (VariableInfo variableInfo : localVars) {

				Type type = variableInfo.getType();
				if (variableInfo.getIndex() == 0) {
					continue;// TODO
				}
				if (type.equals(INT_TYPE) || type.equals(SHORT_TYPE)
						|| type.equals(CHAR_TYPE) || type.equals(BOOLEAN_TYPE)
						|| type.equals(BYTE_TYPE)) {
					mv.visitInsn(ICONST_0);
				} else if (type.equals(FLOAT_TYPE)) {
					mv.visitInsn(FCONST_0);
				} else if (type.equals(DOUBLE_TYPE)) {
					mv.visitInsn(DCONST_0);
				} else if (type.equals(LONG_TYPE)) {
					mv.visitInsn(LCONST_0);
				} else {
					assert type.equals(OBJECT);
					mv.visitInsn(ACONST_NULL);
				}
				int storeOpcode = type.getOpcode(ISTORE);
				mv.visitVarInsn(storeOpcode, variableInfo.getIndex());
			}
		}
	}
}
