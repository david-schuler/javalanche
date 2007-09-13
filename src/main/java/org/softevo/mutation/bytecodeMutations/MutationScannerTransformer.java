package org.softevo.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class MutationScannerTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(MutationScannerTransformer.class);

	private final MutationPossibilityCollector mpc;

	public MutationScannerTransformer(MutationPossibilityCollector mpc) {
		this.mpc = mpc;

	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		logger.info("Mutation Transformer");
		return new MutationsCollectorClassAdapter(new TraceClassVisitor(cc, new PrintWriter(System.out)), mpc);
	}
}
