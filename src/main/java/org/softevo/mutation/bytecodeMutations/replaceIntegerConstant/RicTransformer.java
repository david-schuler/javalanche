package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class RicTransformer extends BytecodeTransformer {

	private static Logger logger = Logger.getLogger(RicTransformer.class);

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
		return new RicClassAdapter(cc);
	}
}
