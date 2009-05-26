package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Class that serve as a function object to insert mutated code using a given
 * {@link MethodVisitor}.
 * 
 * @author David Schuler
 * 
 */
public abstract class MutationCode {

	protected Mutation mutation;

	/**
	 * @param mv
	 */
	public abstract void insertCodeBlock(MethodVisitor mv);

	/**
	 * Returns the underlying mutation.
	 * 
	 * @return the underlying mutation.
	 */
	public Mutation getMutation() {
		return mutation;
	}

	public MutationCode(Mutation mutation) {
		this.mutation = mutation;
	}

}
