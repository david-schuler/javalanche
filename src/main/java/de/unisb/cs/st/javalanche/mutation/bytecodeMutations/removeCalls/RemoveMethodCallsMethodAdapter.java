package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class RemoveMethodCallsMethodAdapter extends AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(RemoveMethodCallsMethodAdapter.class);

	public RemoveMethodCallsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	private static boolean isStaticCall(int opcode) {
		return opcode == INVOKESTATIC;
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		Mutation queryMutation = new Mutation(className, getLineNumber(),
				getPossibilityForLine(), Mutation.MutationType.REMOVE_CALL,
				isClassInit);
		logger.debug("Found possibility for line " + getLineNumber());
		if (MutationManager.shouldApplyMutation(queryMutation)) {
			Mutation mutationFromDB = QueryManager.getMutation(queryMutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(opcode, owner, name, desc);
				}

			};

			MutationCode mutated = new MutationCode(mutationFromDB) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (name.equals("<init>")) {
						mv.visitInsn(POP);
						mv.visitInsn(POP);
						mv.visitInsn(ACONST_NULL);
					} else {
						popArguments(desc, mv);
						popThisReference(opcode, mv);
						pushDefaultValue(desc, mv, name);
					}
				}

				private void pushDefaultValue(final String desc,
						MethodVisitor mv, String name) {
					Type returnType = Type.getReturnType(desc);
					if (returnType.equals(Type.BOOLEAN_TYPE)) {
						mv.visitInsn(ICONST_0);
					} else if (returnType.equals(Type.BYTE_TYPE)) {
						mv.visitInsn(ICONST_0);
					} else if (returnType.equals(Type.CHAR_TYPE)) {
						mv.visitInsn(ICONST_0);
					} else if (returnType.equals(Type.DOUBLE_TYPE)) {
						mv.visitInsn(DCONST_0);
					} else if (returnType.equals(Type.FLOAT_TYPE)) {
						mv.visitInsn(FCONST_0);
					} else if (returnType.equals(Type.INT_TYPE)) {
						mv.visitInsn(ICONST_0);
					} else if (returnType.equals(Type.LONG_TYPE)) {
						mv.visitInsn(LCONST_0);
					} else if (returnType.equals(Type.SHORT_TYPE)) {
						mv.visitInsn(ICONST_0);
					} else if (returnType.equals(Type.VOID_TYPE)) {
						// System.out.println("VOID METHOD: " + name);
					} else {
						mv.visitInsn(ACONST_NULL);
					}
				}

				private void popThisReference(final int opcode, MethodVisitor mv) {
					if (!isStaticCall(opcode)) {
						mv.visitInsn(POP);
					}
				}

				private void popArguments(final String desc, MethodVisitor mv) {
					Type[] argumentsTypes = Type.getArgumentTypes(desc);
					for (int i = argumentsTypes.length - 1; i >= 0; i--) {
						Type argumentType = argumentsTypes[i];

						if (argumentType.getSize() == 1) {
							mv.visitInsn(POP);
						} else {
							mv.visitInsn(POP2);
						}
					}
				}
			};
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			logger.debug("Not applying mutation");
			mv.visitMethodInsn(opcode, owner, name, desc);
		}

	}

	protected Object getInt() {
		// TODO Auto-generated method stub
		return null;
	}
}
