package de.st.cs.unisb.javalanche.bytecodeMutations.removeSystemExit;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import de.st.cs.unisb.javalanche.bytecodeMutations.MutationMarker;

public class RemoveSystemExitMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger
			.getLogger(RemoveSystemExitMethodAdapter.class);

	public RemoveSystemExitMethodAdapter(MethodVisitor mv) {
		super(mv);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		if (name.equals("exit") && owner.equals("java/lang/System")) {
			logger.info("Replacing System.exit ");

			Label mutationStartLabel = new Label();
			mutationStartLabel.info = new MutationMarker(true);
			mv.visitLabel(mutationStartLabel);

			mv.visitInsn(Opcodes.POP);
			mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn("Replaced System Exit");
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
					"java/lang/RuntimeException", "<init>",
					"(Ljava/lang/String;)V");
			mv.visitInsn(Opcodes.ATHROW);

			Label mutationEndLabel = new Label();
			mutationEndLabel.info = new MutationMarker(false);
			mv.visitLabel(mutationEndLabel);

		} else {
			super.visitMethodInsn(opcode, owner, name, desc);
		}
	}

}
