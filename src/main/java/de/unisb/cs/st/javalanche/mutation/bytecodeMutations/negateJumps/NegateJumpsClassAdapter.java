package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class NegateJumpsClassAdapter extends ClassAdapter {

	public NegateJumpsClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	private String className;
	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		// String lastMethodName = name;
		MethodVisitor superVisitor = super.visitMethod(access, name, desc,
				signature, exceptions);
		MethodVisitor actualAdapter = new NegateJumpsMethodAdapter(
				superVisitor, className, name, possibilities);
		return actualAdapter;
	}

}
