package org.softevo.mutation.bytecodeMutations.integrateSuite;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;

public class IntegrateSuiteTransformer extends BytecodeTransformer {

	private static class IntegrateSuiteClassAdapter extends ClassAdapter {

		public IntegrateSuiteClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			if (name.equals("suite")) {
				mv =  new IntegrateTestSuiteMethodAdapter(mv);
			}
			return mv;
		}

	}



	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new IntegrateSuiteClassAdapter(cc);
	}
}
