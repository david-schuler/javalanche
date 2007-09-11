package org.softevo.mutation.bytecodeMutations.arithmetic;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ArithmeticReplaceClassAdapter extends ClassAdapter {

	private String className;

	public ArithmeticReplaceClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		return new ArithmeticReplaceMethodAdapter(super.visitMethod(access,
				name, desc, signature, exceptions), className, name);
	}

}
