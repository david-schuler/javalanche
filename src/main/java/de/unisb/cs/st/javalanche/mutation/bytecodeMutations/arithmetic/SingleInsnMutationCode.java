/**
 * 
 */
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

class SingleInsnMutationCode extends MutationCode {

	private int opc;

	public SingleInsnMutationCode(Mutation mutation, int opcode) {
		super(mutation);
		this.opc = opcode;
	}

	@Override
	public void insertCodeBlock(MethodVisitor mv) {
		mv.visitInsn(opc);
	}

}