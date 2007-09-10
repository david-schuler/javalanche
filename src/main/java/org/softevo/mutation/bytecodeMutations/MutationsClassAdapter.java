package org.softevo.mutation.bytecodeMutations;

import java.util.Iterator;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.softevo.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;

public class MutationsClassAdapter extends ClassAdapter {

	private static final class TestMethodAdapter extends MethodNode {
		private final String className;

		private final ClassVisitor cv;

		private final MethodVisitor mv;

		private TestMethodAdapter(int access, String name, String desc,
				String signature, String[] exceptions, String className,
				ClassVisitor cv, MethodVisitor mv) {
			super(access, name, desc, signature, exceptions);
			this.className = className;
			this.cv = cv;
			this.mv = mv;
		}

		@SuppressWarnings("unchecked")
		@Override
		// public void visitEnd() {
		// // System.out.println("instrSize " + instructions.size());
		// InsnList ins = this.instructions;
		// Iterator i = ins.iterator();
		//
		// MethodNode m = new MethodNode(access, name, desc, signature,
		// (String[]) exceptions.toArray(new String[0]));
		//
		// System.out.println("instrSize " + instructions.size());
		// while (i.hasNext()) {
		// m.instructions.add((AbstractInsnNode) i.next());
		// }
		//
		// // m.instructions.add(instructions);
		// System.out.println("instrSize " + instructions.size());
		//
		// MethodNode m2 = new MethodNode(access, name, desc, signature,
		// (String[]) exceptions.toArray(new String[0]));
		// TraceMethodVisitor traceVisitor = new TraceMethodVisitor(null);
		// accept(new NegateJumpsMethodAdapter(traceVisitor, className, name));
		// System.out.println("trace visitor");
		// traceVisitor.print(new PrintWriter(System.out));
		//
		// // accept(new NegateJumpsMethodAdapter(m2, className, name));
		// // System.out.println("instrSizes " + instructions.size() + ":"
		// // + m.instructions.size());
		// // Iterator mi = m.instructions.iterator();
		// // int counter = 1;
		// // while (mi.hasNext() && i.hasNext()) {
		// // System.out.println(counter++ + " Result: " + mi.next() == i
		// // .next());
		// // AbstractInsnNode a = (AbstractInsnNode) mi.next();
		// // if(a.getType() == AbstractInsnNode.LDC_INSN){
		// // LdcInsnNode ldcNode = (LdcInsnNode) a;
		// // // logger.info(ldcNode.cst);
		// // }
		//
		// // }
		// System.out.println("instrSizes " + instructions.size() + ":"
		// + m.instructions.size() + ":" + m2.instructions.size());
		// accept(cv);
		// }
		public void visitEnd() {
			Iterator it = instructions.iterator();
			int size = 0;
			while (it.hasNext()) {
				AbstractInsnNode insnNode = (AbstractInsnNode) it.next();
				if (insnNode instanceof LabelNode) {
					LabelNode labelNode = (LabelNode) insnNode;
					System.out.println("LabelNode " + labelNode +  "  " +labelNode.getNext());
				}
			}
			MethodNode instrumentedNode = new MethodNode(access, name, desc,
					signature, (String[]) exceptions.toArray(new String[0]));
			accept(new NegateJumpsMethodAdapter(instrumentedNode, className,
					name));
			System.out.println("Instruction sizes " + instructions.size() + ":"
					+ instrumentedNode.instructions.size() + ":" + size); // result
																			// as
																			// expected

			Iterator mit = instrumentedNode.instructions.iterator();
			while (mit.hasNext()) {
				AbstractInsnNode insnNode = (AbstractInsnNode) mit.next();
				if (insnNode instanceof LabelNode) {
					LabelNode labelNode = (LabelNode) insnNode;
					System.out.println("Instrumented LabelNode " + labelNode +"  " +labelNode.getNext());
				}
			}
			accept(cv);
		}
	}

	private String className;

	// private static Logger logger =
	// Logger.getLogger(MutationsClassAdapter.class);

	public MutationsClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, final String[] exceptions) {
		// MethodVisitor superMv =super.visitMethod(access, name, desc,
		// signature, exceptions);
		return new TestMethodAdapter(access, name, desc, signature, exceptions,
				className, cv, null);
	}
}
