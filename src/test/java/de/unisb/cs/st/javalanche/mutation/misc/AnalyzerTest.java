package de.unisb.cs.st.javalanche.mutation.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import static org.junit.Assert.*;
import static org.objectweb.asm.Opcodes.*;

public class AnalyzerTest {

	private static class ANT extends AnalyzerAdapter {

		public ANT(String owner, int access, String name, String desc,
				MethodVisitor mv) {
			super(owner, access, name, desc, mv);
		}

		@Override
		public void visitTypeInsn(int opcode, String desc) {

			super.visitTypeInsn(opcode, desc);
			if (opcode == NEW) {
				System.out.println("Stack: " + stack);
				Iterator iterator = stack.iterator();
//				while(iterator.hasNext()){
//					System.out.println(((Label) iterator.next()).info);
//				}
//				System.out.println("Locals: " + locals);
				iterator = locals.iterator();
//				while(iterator.hasNext()){
//					System.out.println(iterator.next());
//				}
			}

		}

	}

	private static class AntClassAdapter extends ClassAdapter {

		private String className;

		public AntClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			className = name;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			System.out.println("Method: "  + name);
			return new ANT(className, access, name, desc, mv);
		}

	}

	@Test
	public void testAnalyzer() throws IOException {
		File f = new File("/Users/schuler/workspace/Test/MyClass.class");
		assertTrue(f.exists());
		ClassReader cr = new ClassReader(new FileInputStream(f));
		ClassWriter cw = new ClassWriter(0);
		AntClassAdapter ant = new AntClassAdapter(cw);
		cr.accept(ant, ClassReader.EXPAND_FRAMES);

	}
}
