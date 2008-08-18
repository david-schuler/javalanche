package org.softevo.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

import org.softevo.mutation.javaagent.MutationPreMain;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.MutationProperties;

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
//		return cc ; //FIXME
		return new MutationsCollectorClassAdapter(cc, mpc);
	}
}
