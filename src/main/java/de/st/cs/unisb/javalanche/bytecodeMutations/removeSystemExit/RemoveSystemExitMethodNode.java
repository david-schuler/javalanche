package de.st.cs.unisb.javalanche.bytecodeMutations.removeSystemExit;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import de.st.cs.unisb.javalanche.bytecodeMutations.MutationMarker;

public class RemoveSystemExitMethodNode extends MethodAdapter {
	private static Logger logger = Logger
			.getLogger(RemoveSystemExitMethodNode.class);

	MethodVisitor next;

	public RemoveSystemExitMethodNode(int access, String name, String desc,
			String signature, String[] exceptions, MethodVisitor mv) {
		super(new MethodNode(access, name, desc, signature, exceptions));
		next = mv;
	}

	@SuppressWarnings("unchecked")
	// Call to pre 1.5 Code
	@Override
	public void visitEnd() {
		MethodNode mn = (MethodNode) mv;
		InsnList insns= mn.instructions;
		Iterator i = insns.iterator();
		AbstractInsnNode prev = null;
		InsnList newInstrucionList = new InsnList();
		while (i.hasNext()) {
			boolean addInstruction = true;
			AbstractInsnNode i1 = (AbstractInsnNode) i.next();
			if (i1 instanceof MethodInsnNode) {
				MethodInsnNode methotInsnNode = (MethodInsnNode) i1;
				if (methotInsnNode.name.equals("exit")
						&& methotInsnNode.owner.equals("java/lang/System")) {
					logger.info("Replacing System.exit ");
					newInstrucionList.remove(prev);
					// insns.remove(i1);
					InsnList il = new InsnList();
					Label mutationStartLabel = new Label();
					mutationStartLabel.info = new MutationMarker(true);

					il.add(new LabelNode(mutationStartLabel));
					il.add(new TypeInsnNode(Opcodes.NEW,
							"java/lang/RuntimeException"));
					il.add(new InsnNode(Opcodes.DUP));
					il.add(new LdcInsnNode("Replaced System.exit()"));
					il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
							"java/lang/RuntimeException", "<init>",
							"(Ljava/lang/String;)V"));
					il.add(new InsnNode(Opcodes.ATHROW));

					Label mutationEndLabel = new Label();
					mutationEndLabel.info = new MutationMarker(false);
					il.add(new LabelNode(mutationEndLabel));
					newInstrucionList.add(il);
					addInstruction = false;
				}
			}
			if (addInstruction) {
				try {
					insns.remove(i1);
					newInstrucionList.add(i1);
				} catch (Exception e) {
					logger.error(e);
				}
				// newInstrucionList.add(new InsnNode(Opcodes.ICONST_5));
			}
			prev = i1;
		}
		mn.instructions = newInstrucionList;

		// logger.error("new Imstruction List:" + newInstrucionList.size() + " "
		// + insns.size() + " " + mn.instructions.size());
		mn.accept(next);
	}
}