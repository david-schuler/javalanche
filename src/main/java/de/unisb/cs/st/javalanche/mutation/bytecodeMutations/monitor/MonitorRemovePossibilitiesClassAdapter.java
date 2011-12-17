package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.HashMap;
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
public class MonitorRemovePossibilitiesClassAdapter extends ClassAdapter {
	private String className;

	private final MutationPossibilityCollector mutationPossibilityCollector;

	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	public MonitorRemovePossibilitiesClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector mutationPossibilityCollector) {
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
		return new MonitorRemovePossibilitiesMethodAdapter(super.visitMethod(
				access, name, desc, signature, exceptions), className, name,
				mutationPossibilityCollector, possibilities, desc);
	}

}
