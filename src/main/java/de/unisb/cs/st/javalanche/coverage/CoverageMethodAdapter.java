package de.unisb.cs.st.javalanche.coverage;

import static de.unisb.cs.st.javalanche.coverage.CoverageProperties.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.ds.util.io.XmlIo;

/**
 * @author Bernhard Gruen
 * 
 */
public class CoverageMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger.getLogger(CoverageMethodAdapter.class);

	private String methodName, className;

	private boolean instrumentReturns = CoverageProperties.TRACE_RETURNS;
	private boolean instrumentLine = CoverageProperties.TRACE_LINES;

	// primitive data types
	private enum PDType { LONG, INTEGER, FLOAT, DOUBLE };

	@SuppressWarnings("unchecked")
	private static Map<String, Long> profilerMap =  (Map<String, Long>) (new File(TRACE_PROFILER_FILE).exists() ? XmlIo.get(TRACE_PROFILER_FILE) : null);

	@SuppressWarnings("unchecked")
	private static Set<String> dontInstrumentSet =  (Set<String>) (new File(CoverageProperties.TRACE_DONT_INSTRUMENT_FILE).exists() ? XmlIo.get(CoverageProperties.TRACE_DONT_INSTRUMENT_FILE) : null);





	public CoverageMethodAdapter(MethodVisitor visitor, String className,	String methodName, String signature, int classAccess, int methodAccess) {
		super(visitor);
		this.className = className.replace('/', '.');
		this.methodName = methodName;
		
		// don't instrument private classes / methods for data coverage
		if ((classAccess & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE || (methodAccess & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
			logger.debug("not instrumenting method " + this.className + "@" + this.methodName + " (cause: only private access)");
			instrumentReturns = false;
			// instrumentLine = false;
		}

		// don't instrument classes for data coverage that throw an exception
		if (dontInstrumentSet != null && dontInstrumentSet.contains(this.className + "@" + this.methodName)) {
			instrumentReturns = false;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitCode()
	 */
	public void visitCode() {
		if (!methodName.equals("<clinit>")
				&& (instrumentLine || instrumentReturns)) {
			// System.out.println(className + "." + methodName);
			this.visitMethodInsn(Opcodes.INVOKESTATIC, CoverageProperties.TRACER_CLASS_NAME, "getInstance", "()L"+ CoverageProperties.TRACER_CLASS_NAME + ";");
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitLdcInsn(instrumentLine);
			this.visitLdcInsn(instrumentReturns);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CoverageProperties.TRACER_CLASS_NAME, "begin", "(Ljava/lang/String;Ljava/lang/String;ZZ)V");
		}
		super.visitCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.MethodAdapter#visitInsn(int)
	 */
	public void visitInsn(int inst) {
		if (!methodName.equals("<clinit>") && instrumentReturns) {
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
			this.visitMethodInsn(Opcodes.INVOKESTATIC, CoverageProperties.TRACER_CLASS_NAME, "getInstance", "()L" + CoverageProperties.TRACER_CLASS_NAME +";");
			this.visitLdcInsn(line);
			this.visitLdcInsn(className);
			this.visitLdcInsn(methodName);
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CoverageProperties.TRACER_CLASS_NAME, "logLineNumber", "(ILjava/lang/String;Ljava/lang/String;)V");
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

		this.visitMethodInsn(Opcodes.INVOKESTATIC, CoverageProperties.TRACER_CLASS_NAME, "getInstance", "()L" + CoverageProperties.TRACER_CLASS_NAME  +";");
		this.visitInsn(Opcodes.SWAP);
		this.visitLdcInsn(className);
		this.visitLdcInsn(methodName);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CoverageProperties.TRACER_CLASS_NAME, name, "(ILjava/lang/String;Ljava/lang/String;)V");
	}

	private void callLogIReturn() {
		callLogPrototype("logIReturn", PDType.INTEGER);
	}

	private void callLogAReturn() {
		this.visitInsn(Opcodes.DUP);
		this.visitMethodInsn(Opcodes.INVOKESTATIC, CoverageProperties.TRACER_CLASS_NAME, "getInstance", "()L" + CoverageProperties.TRACER_CLASS_NAME  +";");
		this.visitInsn(Opcodes.SWAP);
		this.visitLdcInsn(className);
		this.visitLdcInsn(methodName);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CoverageProperties.TRACER_CLASS_NAME, "logAReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V");
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
