package de.unisb.cs.st.javalanche.tracer;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TracerMethodAdapter extends MethodAdapter {

	private String methodName, className, signature;

	public TracerMethodAdapter(MethodVisitor visitor, String className,	String methodName, String signature) {
		super(visitor);
		this.className = className.replace('/', '.');
		this.methodName = methodName;
		this.signature = signature;
	}

	/*
	 * (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		if (!methodName.equals("<clinit>")) {
			//System.out.println(className + "." + methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L"+ TracerConstants.TRACER_CLASS_NAME + ";");
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "begin", "(Ljava/lang/String;Ljava/lang/String;)V");
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
			case Opcodes.IRETURN:				
				this.visitInsn(Opcodes.DUP);
				this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME  +";");
				this.visitInsn(Opcodes.SWAP);
				this.visitLdcInsn(className);
				this.visitLdcInsn(methodName);
				this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "logIReturn", "(ILjava/lang/String;Ljava/lang/String;)V");
			case Opcodes.ARETURN:
			case Opcodes.ATHROW:
			case Opcodes.DRETURN:
			case Opcodes.FRETURN:
			case Opcodes.LRETURN:
			case Opcodes.RETURN:
				this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME +";");
				this.visitLdcInsn(className);
				this.visitLdcInsn(methodName);
				
				this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "end", "(Ljava/lang/String;Ljava/lang/String;)V");
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
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME +";");
			this.visitLdcInsn(line);
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "logLineNumber", "(ILjava/lang/String;Ljava/lang/String;)V");
		}
		super.visitLineNumber(line, start);
	 }
}