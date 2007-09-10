package org.softevo.mutation.bytecodeMutations.aritmeticReplace;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


public class PossibilitiesAretmeticReplaceClassAdapter extends ClassAdapter {

	public PossibilitiesAretmeticReplaceClassAdapter(ClassVisitor cv) {
		super(cv);
	}
	
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new PossibilitiesAretmeticReplaceMethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
	}
}
