package de.unisb.cs.st.javalanche.tracer;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TracerMethodAdapter extends MethodAdapter {

	private String methodName, className, signature;

	// primitive data types
	private enum PDType { LONG, INTEGER, FLOAT, DOUBLE };

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
				callLogIReturn();
				callEnd();
				break;
			case Opcodes.ARETURN:
				callLogAReturn();
				callEnd();
				break;
			case Opcodes.ATHROW:
				callEnd();
				break;
			case Opcodes.DRETURN:
				callLogDReturn();
				callEnd();
				break;
			case Opcodes.FRETURN:
				callLogFReturn();
				callEnd();
				break;
			case Opcodes.LRETURN:
				callLogLReturn();
				callEnd();
				break;
			case Opcodes.RETURN:
				callEnd();
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

	private void callLogPrototype(String name, PDType type) {
		if (type != PDType.LONG && type != PDType.DOUBLE) {
			this.visitInsn(Opcodes.DUP);
			if (type == PDType.FLOAT) {
				this.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "floatToRawIntBits", "(F)I");
			}
		} else {
			this.visitInsn(Opcodes.DUP2);
			if (type == PDType.DOUBLE) {
				this.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "doubleToRawLongBits", "(D)J");
			}
			this.visitInsn(Opcodes.DUP2);
			this.visitIntInsn(Opcodes.BIPUSH, 32);
			this.visitInsn(Opcodes.LSHR);
			this.visitInsn(Opcodes.LXOR);
			this.visitInsn(Opcodes.L2I);
		}

		this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME  +";");
		this.visitInsn(Opcodes.SWAP);
		this.visitLdcInsn(className);
		this.visitLdcInsn(methodName);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, name, "(ILjava/lang/String;Ljava/lang/String;)V");
	}

	private void callLogIReturn() {
		callLogPrototype("logIReturn", PDType.INTEGER);
	}

	private void callLogAReturn() {
		this.visitInsn(Opcodes.DUP);
		this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME  +";");
		this.visitInsn(Opcodes.SWAP);
		this.visitLdcInsn(className);
		this.visitLdcInsn(methodName);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "logAReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V");
	}


	private void callLogLReturn() {
		callLogPrototype("logLReturn", PDType.LONG);
	}

	private void callLogDReturn() {
		callLogPrototype("logDReturn", PDType.DOUBLE);
	}

	private void callLogFReturn() {
		callLogPrototype("logFReturn", PDType.FLOAT);
	}


	private void callEnd() {
		/*
		this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" + TracerConstants.TRACER_CLASS_NAME +";");
		this.visitLdcInsn(className);
		this.visitLdcInsn(methodName);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "end", "(Ljava/lang/String;Ljava/lang/String;)V");
		*/
	}
}