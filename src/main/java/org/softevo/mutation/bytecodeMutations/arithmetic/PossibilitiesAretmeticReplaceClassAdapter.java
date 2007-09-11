package org.softevo.mutation.bytecodeMutations.arithmetic;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class PossibilitiesAretmeticReplaceClassAdapter extends ClassAdapter {
	private String className;
	private final MutationPossibilityCollector mutationPossibilityCollector;
	public PossibilitiesAretmeticReplaceClassAdapter(ClassVisitor cv, MutationPossibilityCollector mutationPossibilityCollector) {
		super(cv);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
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
		return new PossibilitiesAretmeticReplaceMethodAdapter(super
				.visitMethod(access, name, desc, signature, exceptions),className, name, mutationPossibilityCollector);
	}
}
