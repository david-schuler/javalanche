package org.softevo.mutation.bytecodeMutations;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.softevo.mutation.bytecodeMutations.arithmetic.ArithmeticReplaceMethodAdapter;
import org.softevo.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicMethodAdapter;

public class MutationsClassAdapter extends ClassAdapter {

	private String className;

	private static final boolean DEBUG = true;

	public MutationsClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		if (DEBUG) {
			mv = new CheckMethodAdapter(mv);
		}
		mv = new RicMethodAdapter(mv, className, name);
		mv = new NegateJumpsMethodAdapter(mv, className, name);
		mv = new ArithmeticReplaceMethodAdapter(mv, className, name);
		return mv;
	}
}
