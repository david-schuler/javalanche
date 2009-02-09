package de.unisb.cs.st.javalanche.tracer;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TracerMethodAdapter extends MethodAdapter {

	private static final String TRACER_CLASS_NAME = "de/unisb/cs/st/javalanche/tracer/Trace";
	private String methodName, className;

	public TracerMethodAdapter(MethodVisitor visitor, String className,	String methodName) {
		super(visitor);
		this.className = className.replace('/', '.');
		this.methodName = methodName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		if (!methodName.equals("<clinit>")) {
			//System.out.println(className + "." + methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TRACER_CLASS_NAME, "getInstance", "()L"+ TRACER_CLASS_NAME + ";");
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACER_CLASS_NAME, "begin", "(Ljava/lang/String;Ljava/lang/String;)V");
		}
		super.visitCode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitInsn(int)
	 */
	public void visitInsn(int inst) {
		if (!methodName.equals("<clinit>")) {
			switch (inst) {
			case Opcodes.ARETURN:
			case Opcodes.ATHROW:
			case Opcodes.DRETURN:
			case Opcodes.FRETURN:
			case Opcodes.IRETURN:
			case Opcodes.LRETURN:
			case Opcodes.RETURN:
				this.visitMethodInsn(Opcodes.INVOKESTATIC, TRACER_CLASS_NAME, "getInstance", "()L" + TRACER_CLASS_NAME   +";");
				this.visitLdcInsn(className);
				this.visitLdcInsn(methodName);
				this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACER_CLASS_NAME, "end", "(Ljava/lang/String;Ljava/lang/String;)V");
				break;
			default:
				break;
			}
		}
		super.visitInsn(inst);
	}

	/*
	 * (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitLineNumber(int, org.objectweb.asm.Label)
	 */
	public void visitLineNumber(int line, Label start) {
		if (!methodName.equals("<clinit>")) {
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TRACER_CLASS_NAME, "getInstance", "()L" + TRACER_CLASS_NAME   +";");
			this.visitLdcInsn(line);
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACER_CLASS_NAME, "logLineNumber", "(ILjava/lang/String;Ljava/lang/String;)V");
		}
		super.visitLineNumber(line, start);
	 }
}