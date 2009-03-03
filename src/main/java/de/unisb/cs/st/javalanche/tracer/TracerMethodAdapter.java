package de.unisb.cs.st.javalanche.tracer;

import static de.unisb.cs.st.javalanche.tracer.TracerConstants.TRACE_PROFILER_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class TracerMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger.getLogger(TracerMethodAdapter.class);

	private String methodName, className, signature;

	private boolean instrumentData = true;
	private boolean instrumentLine = true;

	// primitive data types
	private enum PDType { LONG, INTEGER, FLOAT, DOUBLE };

	@SuppressWarnings("unchecked")
	private static Map<String, Long> profilerMap =  (Map<String, Long>) (new File(TRACE_PROFILER_FILE).exists() ? XmlIo.get(TRACE_PROFILER_FILE) : null);

	@SuppressWarnings("unchecked")
	private static Set<String> dontInstrumentSet =  (Set<String>) (new File(TracerConstants.TRACE_DONT_INSTRUMENT_FILE).exists() ? XmlIo.get(TracerConstants.TRACE_DONT_INSTRUMENT_FILE) : null);


	private static final long PERCENT_BOUND = percentBound();



	private static long percentBound() {
		if(profilerMap == null ||TracerConstants.TRACE_PROFILER_PERCENT >= 100){
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

	public TracerMethodAdapter(MethodVisitor visitor, String className,	String methodName, String signature, int classAccess, int methodAccess) {
		super(visitor);
		this.className = className.replace('/', '.');
		this.methodName = methodName;
		this.signature = signature;

		// don't instrument private classes / methods for data coverage
		if ((classAccess & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE || (methodAccess & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
			logger.debug("not instrumenting method " + this.className + "@" + this.methodName + " (cause: only private access)");
			instrumentData = false;
			//instrumentLine = false;
		}

		// don't instrument classes for data coverage that throw an exception
		if (dontInstrumentSet != null && dontInstrumentSet.contains(this.className + "@" + this.methodName)) {
			instrumentData = false;
		}

		// don't instrument some classes for data coverage
		try {
			if (profilerMap != null /*RUN_MODE != CREATE_COVERAGE*/) {
				Long calls = profilerMap.get(this.className + "@" + methodName);
				if (calls != null) {
					if (calls >= TracerConstants.TRACE_PROFILER_MAX_CALLS  || calls >= PERCENT_BOUND) {
						logger.debug("not instrumenting method " + this.className + "@" + this.methodName + " (cause: too much calls to it)");
						instrumentData = false;
						//instrumentLine = false;
					}
				} else {
					logger.debug("method " + this.className + "@" + this.methodName + " not in profiler map");
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		if (!methodName.equals("<clinit>") && (instrumentLine || instrumentData)) {
			// System.out.println(className + "." + methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, TracerConstants.TRACER_CLASS_NAME, "getInstance", "()L"+ TracerConstants.TRACER_CLASS_NAME + ";");
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitLdcInsn(instrumentLine);
			this.visitLdcInsn(instrumentData);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TracerConstants.TRACER_CLASS_NAME, "begin", "(Ljava/lang/String;Ljava/lang/String;ZZ)V");
		}
		super.visitCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitInsn(int)
	 */
	public void visitInsn(int inst) {
		if (!methodName.equals("<clinit>") && instrumentData) {
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
		if (!methodName.equals("<clinit>") && instrumentLine) {
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
