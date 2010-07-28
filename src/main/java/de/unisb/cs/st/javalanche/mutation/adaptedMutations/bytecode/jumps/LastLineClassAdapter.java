package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LastLineClassAdapter extends ClassAdapter {

	private BytecodeInfo lastLineInfo;
	private String className;

	public LastLineClassAdapter(ClassVisitor cv, BytecodeInfo lastLineInfo) {
		super(cv);
		this.lastLineInfo = lastLineInfo;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
		return new LastLineMethodAdapter(mv, className, name, desc,
				lastLineInfo, isStatic);
	}
}
