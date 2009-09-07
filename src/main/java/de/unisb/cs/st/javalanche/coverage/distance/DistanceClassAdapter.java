package de.unisb.cs.st.javalanche.coverage.distance;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class DistanceClassAdapter extends ClassAdapter {

	private String className;
	private ConnectionData connectionData;

	public DistanceClassAdapter(ClassVisitor cv, ConnectionData connectionData) {
		super(cv);
		this.connectionData = connectionData;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.className = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		return new DistanceMethodAdapter(mv, className, name, desc,
				connectionData);
	}
}
