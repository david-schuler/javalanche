package org.softevo.mutation.replaceIntegerConstant;

import org.objectweb.asm.MethodVisitor;

public interface MutationIfElse {

	public void ifBlock(MethodVisitor mv);

	public void elseBlock(MethodVisitor mv);

}
