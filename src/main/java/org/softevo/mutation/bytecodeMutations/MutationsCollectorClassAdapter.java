package org.softevo.mutation.bytecodeMutations;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.softevo.mutation.bytecodeMutations.arithmetic.PossibilitiesArithmeticReplaceMethodAdapter;
import org.softevo.mutation.bytecodeMutations.negateJumps.NegateJumpsPossibilitiesMethodAdapter;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.PossibilitiesRicMethodAdapter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class MutationsCollectorClassAdapter extends ClassAdapter{
	private String className;

	private boolean debug=true;

	private final MutationPossibilityCollector mpc;

	public MutationsCollectorClassAdapter(ClassVisitor cv, MutationPossibilityCollector mpc) {
		super(cv);
		this.mpc = mpc;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		if (debug) {
			mv = new CheckMethodAdapter(mv);
		}
		mv = new PossibilitiesRicMethodAdapter(mv, className, name, mpc);
		mv = new NegateJumpsPossibilitiesMethodAdapter(mv, className, name,mpc);
		mv = new PossibilitiesArithmeticReplaceMethodAdapter(mv, className, name, mpc);
		return mv;
	}
}
