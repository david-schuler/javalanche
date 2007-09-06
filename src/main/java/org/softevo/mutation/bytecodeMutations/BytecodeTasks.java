package org.softevo.mutation.bytecodeMutations;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicMethodAdapter;
import org.softevo.mutation.results.Mutation;

/**
 * Class that provides static methods for common bytecode modifications.
 *
 * @author David Schuler
 *
 */
public class BytecodeTasks {

	private BytecodeTasks() {
	}

	public static void insertIfElse(MethodVisitor mv, MutationCode unMutated,
			MutationCode[] mutations) {
		RicMethodAdapter.mutationForLine++;
		Label endLabel = new Label();
		for (MutationCode mutationCode : mutations) {
			Mutation mutation = mutationCode.getMutation();
			mv.visitLdcInsn(mutation.getMutationVariable());
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.IFNULL, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			BytecodeTasks.insertPrintStatements(mv, "Mutation "
					+ mutation.getMutationVariable() + " - "
					+ mutation.getMutationType() + "is enabled");
			mutationCode.insertCodeBlock(mv);
			mv.visitJumpInsn(Opcodes.GOTO, endLabel);
			mv.visitLabel(l1);
		}
		unMutated.insertCodeBlock(mv);
		mv.visitLabel(endLabel);
	}

	public static void insertPrintStatements(MethodVisitor mv, String message) {
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[RIC] " + message);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");
	}



}
