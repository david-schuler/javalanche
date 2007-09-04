package org.softevo.mutation.bytecodeMutations;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

public class LineNumberAdapter extends MethodAdapter {

	private int lineNumber = -1;

	protected String className;

	protected String methodName;

	public LineNumberAdapter(MethodVisitor mv, String className,
			String methodName) {
		super(mv);
		this.className = className;
		this.methodName = methodName;
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		lineNumber = line;
	}

	protected int getLineNumber() {
		if (lineNumber < 0) {
			throw new RuntimeException(String.format(
					"Linenumber not available for class: %s method: %s",
					className, methodName));
		}
		return lineNumber;
	}
}
