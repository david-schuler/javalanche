package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationTransformer extends BytecodeTransformer {

	private static Logger logger = Logger.getLogger(MutationTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cv = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(MutationPreMain.sysout));
		}
		return new MutationsClassAdapter(cv);
	}

}
