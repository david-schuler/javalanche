package org.softevo.mutation.bytecodeMutations;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractMutationAdapter extends MethodAdapter {

	private static final Logger logger = Logger
			.getLogger(AbstractMutationAdapter.class);

	private int lineNumber = -1;

	protected String className;

	protected String methodName;

	protected boolean mutationCode = false;

	public AbstractMutationAdapter(MethodVisitor mv, String className,
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
					"Line number not available for class: %s method: %s",
					className, methodName));
		}
		return lineNumber;
	}

	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
		if (label.info instanceof MutationMarker) {
			MutationMarker marker = (MutationMarker) label.info;
			logger.info("Found mutation marker: "
					+ (marker.isStart() ? "start" : "end") + " in line "
					+ getLineNumber() + "  " + this);
			mutationCode = marker.isStart();
		}
	}

}
