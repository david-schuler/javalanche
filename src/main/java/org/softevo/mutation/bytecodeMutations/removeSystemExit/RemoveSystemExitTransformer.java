package org.softevo.mutation.bytecodeMutations.removeSystemExit;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class RemoveSystemExitTransformer extends BytecodeTransformer {

	private static class RemoveSystemExitClassAdapter extends ClassAdapter {

		public RemoveSystemExitClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {

			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			return new RemoveSystemExitMethodNode(access, signature, signature, signature, exceptions, mv);
		}

	}

	private static Logger logger = Logger
			.getLogger(RemoveSystemExitTransformer.class);


	public RemoveSystemExitTransformer() {
		logger.debug("new MutationScannerTransformer");
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new RemoveSystemExitClassAdapter(cc);
	}
}
