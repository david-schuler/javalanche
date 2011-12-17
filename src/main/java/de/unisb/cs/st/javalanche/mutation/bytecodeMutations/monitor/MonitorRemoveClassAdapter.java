package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
class MonitorRemoveClassAdapter extends ClassAdapter {

	private String className;
	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	public MonitorRemoveClassAdapter(ClassVisitor cv) {
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
		return new MonitorRemoveMethodAdapter(super.visitMethod(access, name,
				desc, signature, exceptions), className, name, possibilities,
				new MutationManager(), desc);
	}

}
