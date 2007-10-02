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

	private static final boolean PRINT_STATEMENTS_ENABLED = true;

	private BytecodeTasks() {
	}

	/**
	 * Inserts a mutation. The inserted code is like this:
	 * <code>if(System.getProperty(mutationID)){
	 * 			execute mutated code
	 * 		}
	 * 		else{
	 *			execute unmutated code
	 * 		}
	 * @param mv MethodVisitor where the code is inserted.
	 * @param unMutated code that should be used when no mutation is applied.
	 * @param mutations code that should be used when one of the mutations is applied.
	 */
	public static void insertIfElse(MethodVisitor mv, MutationCode unMutated,
			MutationCode[] mutations) {
		RicMethodAdapter.mutationForLine++;
		Label endLabel = new Label();
		Label mutationStartLabel = new Label();
		mutationStartLabel.info = new MutationMarker(true);
		mv.visitLabel(mutationStartLabel);
		for (MutationCode mutationCode : mutations) {
			Mutation mutation = mutationCode.getMutation();
			mv.visitLdcInsn(mutation.getMutationVariable());
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.IFNULL, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			insertMutationTouchedCode(mv, mutation);
			mutationCode.insertCodeBlock(mv);
			mv.visitJumpInsn(Opcodes.GOTO, endLabel);
			mv.visitLabel(l1);
		}
		Label mutationEndLabel = new Label();
		mutationEndLabel.info = new MutationMarker(false);
		mv.visitLabel(mutationEndLabel);
		unMutated.insertCodeBlock(mv);
		mv.visitLabel(endLabel);

	}

	private static void insertMutationTouchedCode(MethodVisitor mv,
			Mutation mutation) {
		if (PRINT_STATEMENTS_ENABLED) {
			BytecodeTasks.insertPrintStatements(mv, "Mutation "
					+ mutation.getMutationVariable() + " - "
					+ mutation.getMutationType() + " is enabled");
		}
		mv.visitLdcInsn(mutation.getId());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC,
				"org/softevo/mutation/testsuite/ResultReporter", "touch",
				"(I)V");
	}

	/**
	 * Inserts bytecode that prints the given message.
	 *
	 * @param mv
	 *            The MethodVisitor for which teh code is added.
	 * @param message
	 *            The text to be printed to System.out .
	 */
	public static void insertPrintStatements(MethodVisitor mv, String message) {
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[MUTATION] " + message);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");
	}

}
