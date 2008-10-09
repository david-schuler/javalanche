package de.st.cs.unisb.javalanche.bytecodeMutations.replaceIntegerConstant;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import de.st.cs.unisb.javalanche.mutationPossibilities.MutationPossibilityCollector;

public class PossibilitiesRicClassAdapter extends ClassAdapter {

	private PossibilitiesRicMethodAdapter actualAdapter;

	private String className;

	private MutationPossibilityCollector mutationPossibilityCollector;

	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	public PossibilitiesRicClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector collector) {
		super(cv);
		this.mutationPossibilityCollector = collector;
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
		actualAdapter = new PossibilitiesRicMethodAdapter(super.visitMethod(
				access, name, desc, signature, exceptions), className, name,
				mutationPossibilityCollector, possibilities);
		return actualAdapter;

	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

}
