package de.unisb.cs.st.javalanche.coverage.distance;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class DistanceMethodAdapter extends MethodAdapter {

	private final String className;
	private final String methodName;
	private final String description;
	private ConnectionData connectionData;

	public DistanceMethodAdapter(MethodVisitor mv, String className,
			String methodName, String description, ConnectionData connectionData) {
		super(mv);
		this.className = className;
		this.methodName = methodName;
		this.description = description;
		this.connectionData = connectionData;

	}

	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		String ownerDots = owner.replace('/', '.');
		if (ownerDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
			connectionData.addConnection(className, methodName, description,
					owner, name, desc);
		}
	}

}
