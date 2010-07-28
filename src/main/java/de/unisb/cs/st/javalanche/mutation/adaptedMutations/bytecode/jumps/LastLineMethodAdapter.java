package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class LastLineMethodAdapter extends MethodAdapter {

	private final String desc;
	private final String name;
	private final BytecodeInfo bytecodeInfo;
	private int lastLine;
	private String className;
	private final boolean isStatic;
	private int startIndex;

	public LastLineMethodAdapter(MethodVisitor mv, String className,
			String name, String desc, BytecodeInfo lastLineInfo,
			boolean isStatic) {
		super(mv);
		this.className = className;
		this.name = name;
		this.desc = desc;
		this.bytecodeInfo = lastLineInfo;
		this.isStatic = isStatic;
		startIndex = isStatic ? 0 : 1;
		Type[] argumentTypes = Type.getArgumentTypes(desc);
		for (Type type : argumentTypes) {
			startIndex += type.getSize();
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		lastLine = line;
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitEnd() {
		bytecodeInfo.addLastLine(className, name, desc, lastLine);
		super.visitEnd();
	}

	@Override
	public void visitLocalVariable(String vname, String vdesc,
			String signature, Label start, Label end, int index) {
		if (index >= startIndex) {
			bytecodeInfo.addLocalVar(className, name, desc, vname, vdesc,
					start, end, index);
		}
		super.visitLocalVariable(vname, vdesc, signature, start, end, index);
	}
}
