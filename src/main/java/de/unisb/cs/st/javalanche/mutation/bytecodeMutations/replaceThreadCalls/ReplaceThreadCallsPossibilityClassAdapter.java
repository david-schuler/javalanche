package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public class ReplaceThreadCallsPossibilityClassAdapter extends ClassAdapter {

	private String className;

	private final MutationPossibilityCollector mpc;

	private final Map<Integer, Integer> possibilityMap;

	public ReplaceThreadCallsPossibilityClassAdapter(ClassVisitor cv,
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

		return new ReplaceThreadCallsPossibilitiesMethodAdapter(
				new ReplaceAdviceAdapter(mv, access, name, desc), className,
				name, mpc, possibilityMap, desc);
	}
}
