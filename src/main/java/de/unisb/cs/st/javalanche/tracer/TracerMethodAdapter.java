package de.unisb.cs.st.javalanche.tracer;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class TracerMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger.getLogger(TracerMethodAdapter.class);

	private String methodName, className, signature;

	private boolean instrument = true;

	// primitive data types
	private enum PDType { LONG, INTEGER, FLOAT, DOUBLE };

	@SuppressWarnings("unchecked")
	private static Map<String, Long> profilerMap =  (Map<String, Long>) (RUN_MODE != CREATE_COVERAGE ? XmlIo.get(TracerConstants.TRACE_PROFILER_FILE) : null);


	private static final long PERCENT_BOUND = percentBound();



	private static long percentBound() {
		if(RUN_MODE == CREATE_COVERAGE ||TracerConstants.TRACE_PROFILER_PERCENT >= 100){
			return Long.MAX_VALUE;
		}
		try{
			List<Long> values = new ArrayList<Long>(profilerMap.values());
			Collections.sort(values);
			int boundPos = (int) ( values.size() * ( TracerConstants.TRACE_PROFILER_PERCENT / 100. ));
			if(boundPos < values.size()){
				long bound = values.get(boundPos);
				logger.info("Bound for "+ TracerConstants.TRACE_PROFILER_PERCENT + " percent:  " + bound   + " at position " + boundPos + " of " + values.size());
				return bound;
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return 0l;
	}

	public TracerMethodAdapter(MethodVisitor visitor, String className,	String methodName, String signature) {
		super(visitor);
		this.className = className.replace('/', '.');
		this.methodName = methodName;
		this.signature = signature;

		if (RUN_MODE != CREATE_COVERAGE) {
			Long calls = profilerMap.get(className + "@" + methodName);
			if (calls >= TracerConstants.TRACE_PROFILER_MAX_CALLS  && calls >= PERCENT_BOUND){
				instrument = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		if (!methodName.equals("<clinit>") && instrument) {
			// System.out.println(className + "." + methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L"+ TracerConstants.TRACER_CLASS_NAME + ";");
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "begin", "(Ljava/lang/String;Ljava/lang/String;)V");
		}
		super.visitCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitInsn(int)
	 */
	public void visitInsn(int inst) {
		if (!methodName.equals("<clinit>") && instrument) {
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
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitLineNumber(int,
	 *      org.objectweb.asm.Label)
	 */
	public void visitLineNumber(int line, Label start) {
		if (!methodName.equals("<clinit>") && instrument) {
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
		 * this.visitMethodInsn(Opcodes.INVOKESTATIC,
		 * TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L" +
		 * TracerConstants.TRACER_CLASS_NAME +";");
		 * this.visitLdcInsn(className); this.visitLdcInsn(methodName);
		 * this.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
		 * TracerConstants.TRACER_CLASS_NAME, "end",
		 * "(Ljava/lang/String;Ljava/lang/String;)V");
		 */
	}
}