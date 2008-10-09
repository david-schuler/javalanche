package de.st.cs.unisb.javalanche.bytecodeMutations.replaceIntegerConstant;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckMethodAdapter;

public class RicClassAdapter extends ClassAdapter {

	private String className;

	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	public RicClassAdapter(ClassVisitor cv) {
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
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		return new CheckMethodAdapter(new RicMethodAdapter(mv, className, name,
				possibilities));
	}
}
