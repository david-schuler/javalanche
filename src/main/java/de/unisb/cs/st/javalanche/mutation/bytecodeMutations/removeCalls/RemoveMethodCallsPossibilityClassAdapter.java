package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

public class RemoveMethodCallsPossibilityClassAdapter extends ClassAdapter {

	private static Logger logger = Logger
			.getLogger(RemoveMethodCallsPossibilityClassAdapter.class);

	private String className;

	private final MutationPossibilityCollector mpc;

	private final Map<Integer, Integer> possibilityMap;

	public RemoveMethodCallsPossibilityClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilityMap) {
		super(cv);
		this.mpc = mpc;
		this.possibilityMap = possibilityMap;
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
		
		return new RemoveCallsPossibilitiesMethodAdapter(new MyAdviceAdapter(
				mv, access, name, desc), className, name, mpc, possibilityMap);
	}
}
