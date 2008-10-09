package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class NegateJumpsPossibilitiesClassAdapter extends ClassAdapter {

	private String className;

	private final MutationPossibilityCollector mpc;

	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	public NegateJumpsPossibilitiesClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector mpc) {
		super(cv);
		this.mpc = mpc;
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
		// String lastMethodName = name;
		MethodVisitor superVisitor = super.visitMethod(access, name, desc,
				signature, exceptions);
		MethodVisitor actualAdapter = new NegateJumpsPossibilitiesMethodAdapter(
				superVisitor, className, name, mpc,possibilities );
		return actualAdapter;
	}



}
