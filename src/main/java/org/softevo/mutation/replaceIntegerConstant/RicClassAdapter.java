package org.softevo.mutation.replaceIntegerConstant;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class RicClassAdapter extends ClassAdapter {

	private String className;

	private final Mutations mutationsToApply;

	public RicClassAdapter(ClassVisitor cv, Mutations mutationsToApply) {
		super(cv);
		this.mutationsToApply = mutationsToApply;
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
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		return new CheckMethodAdapter(new RicMethodAdapter(mv, className, name, mutationsToApply));
	}
}
