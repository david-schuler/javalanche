package de.st.cs.unisb.javalanche.bytecodeMutations;

import org.objectweb.asm.MethodVisitor;
import de.st.cs.unisb.javalanche.results.Mutation;

public abstract class MutationCode {

	protected Mutation mutation;

	public abstract void insertCodeBlock(MethodVisitor mv);

	/**
	 * @return the mutation
	 */
	public Mutation getMutation() {
		return mutation;
	}

	public MutationCode(Mutation mutation) {
		this.mutation = mutation;
	}

	public MutationCode() {
	}

}
