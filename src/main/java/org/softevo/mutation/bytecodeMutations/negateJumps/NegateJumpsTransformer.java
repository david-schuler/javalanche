package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;

public class NegateJumpsTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(NegateJumpsTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		PrintWriter p = new PrintWriter(System.out);
		cc = new TraceClassVisitor(cc, p);

		NegateJumpsClassAdapter njca = new NegateJumpsClassAdapter(cc);
		logger.info("return Class visitor" + njca);
		return njca;
	}

}
