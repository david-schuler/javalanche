///*
//* Copyright (C) 2011 Saarland University
//* 
//* This file is part of Javalanche.
//* 
//* Javalanche is free software: you can redistribute it and/or modify
//* it under the terms of the GNU Lesser Public License as published by
//* the Free Software Foundation, either version 3 of the License, or
//* (at your option) any later version.
//* 
//* Javalanche is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* GNU Lesser Public License for more details.
//* 
//* You should have received a copy of the GNU Lesser Public License
//* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
//*/
//package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;
//
//import org.apache.log4j.Logger;
//import org.objectweb.asm.AnnotationVisitor;
//import org.objectweb.asm.Attribute;
//import org.objectweb.asm.ClassAdapter;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.Label;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
//
//public class ModifyJunit4Adaper extends ClassAdapter {
//
//	private static Logger logger = Logger.getLogger(ModifyJunit4Adaper.class);
//
//	public ModifyJunit4Adaper(ClassVisitor cw) {
//		super(cw);
//	}
//
//	@Override
//	public MethodVisitor visitMethod(int access, String name, String desc,
//			String signature, String[] exceptions) {
//		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
//				exceptions);
//		if (name.equals("run")) {
//			logger.debug("Found run in " + JUNIT4_TEST_ADAPTER);
//			mv = new ModifyMethodAdapater(mv);
//		}
//		return mv;
//	}
//
//	private static class ModifyMethodAdapater implements MethodVisitor, Opcodes {
//
//		private final MethodVisitor mv;
//
//		public ModifyMethodAdapater(MethodVisitor mv) {
//			this.mv = mv;
//		}
//
//		public void visitCode() {
//			logger.debug("Modifying the run Method of " + JUNIT4_TEST_ADAPTER);
//			mv.visitCode();
//			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
//					"Ljava/io/PrintStream;");
//			mv.visitLdcInsn("Running modified JUnit 4 tests");
//			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
//					"(Ljava/lang/String;)V");
//			
//			
//			mv
//					.visitTypeInsn(
//							NEW,
//							"de/unisb/cs/st/javalanche/mutation/runtime/testDriver/junit/Junit4MutationTestDriver");
//			mv.visitInsn(DUP);
//			mv
//					.visitMethodInsn(
//							INVOKESPECIAL,
//							"de/unisb/cs/st/javalanche/mutation/runtime/testDriver/junit/Junit4MutationTestDriver",
//							"<init>", "()V");
//			mv.visitVarInsn(ASTORE, 1);
//			mv.visitVarInsn(ALOAD, 1);
//			mv
//					.visitMethodInsn(
//							INVOKEVIRTUAL,
//							"de/unisb/cs/st/javalanche/mutation/runtime/testDriver/junit/Junit4MutationTestDriver",
//							"run", "()V");
//
//			mv.visitInsn(RETURN);
//		}
//
//		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//			return mv.visitAnnotation(desc, visible);
//		}
//
//		public AnnotationVisitor visitAnnotationDefault() {
//			return null;
//		}
//
//		public void visitAttribute(Attribute attr) {
//
//		}
//
//		public void visitEnd() {
//			mv.visitEnd();
//		}
//
//		public void visitFieldInsn(int opcode, String owner, String name,
//				String desc) {
//		}
//
//		public void visitFrame(int type, int local, Object[] local2, int stack,
//				Object[] stack2) {
//
//		}
//
//		public void visitIincInsn(int var, int increment) {
//		}
//
//		public void visitInsn(int opcode) {
//		}
//
//		public void visitIntInsn(int opcode, int operand) {
//		}
//
//		public void visitJumpInsn(int opcode, Label label) {
//		}
//
//		public void visitLabel(Label label) {
//		}
//
//		public void visitLdcInsn(Object cst) {
//		}
//
//		public void visitLineNumber(int line, Label start) {
//		}
//
//		public void visitLocalVariable(String name, String desc,
//				String signature, Label start, Label end, int index) {
//		}
//
//		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
//		}
//
//		public void visitMaxs(int maxStack, int maxLocals) {
//			mv.visitMaxs(maxStack, maxLocals);
//		}
//
//		public void visitMethodInsn(int opcode, String owner, String name,
//				String desc) {
//
//		}
//
//		public void visitMultiANewArrayInsn(String desc, int dims) {
//		}
//
//		public AnnotationVisitor visitParameterAnnotation(int parameter,
//				String desc, boolean visible) {
//			return null;
//		}
//
//		public void visitTableSwitchInsn(int min, int max, Label dflt,
//				Label[] labels) {
//		}
//
//		public void visitTryCatchBlock(Label start, Label end, Label handler,
//				String type) {
//		}
//
//		public void visitTypeInsn(int opcode, String desc) {
//		}
//
//		public void visitVarInsn(int opcode, int var) {
//		}
//
//	}
// }
