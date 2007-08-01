package org.softevo.mutation.replaceIntegerConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class RicTransformer extends BytecodeTransformer {

	private static Logger logger = Logger.getLogger(RicTransformer.class.getName());

	private Mutations mutations;

	public RicTransformer(Mutations mutations) {
		super();
		this.mutations = mutations;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		try {
			File f = new File("trace-visitor-out.txt");
			logger.info(f.getAbsolutePath());
			System.out.println(f.getAbsolutePath());
			cc = new TraceClassVisitor(cc, new PrintWriter(new FileWriter(f)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("could not write to file");
		}
		return new RicClassAdapter(cc, mutations);
	}
}
