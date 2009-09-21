package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class RicTransformer extends BytecodeTransformer {

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		cc = new TraceClassVisitor(cc, new PrintWriter(System.out));
		return new RicClassAdapter(cc);
	}
}
