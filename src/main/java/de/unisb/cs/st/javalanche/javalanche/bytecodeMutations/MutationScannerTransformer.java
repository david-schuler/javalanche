package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class MutationScannerTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(MutationScannerTransformer.class);

	private final MutationPossibilityCollector mpc;

	public MutationScannerTransformer(MutationPossibilityCollector mpc) {
		this.mpc = mpc;
		logger.debug("new MutationScannerTransformer");
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cc = new TraceClassVisitor(cc, new PrintWriter(MutationPreMain.sysout));
		}
		return new MutationsCollectorClassAdapter(cc, mpc);
	}
}
