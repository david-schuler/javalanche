package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

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
