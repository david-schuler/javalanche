package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

/**
 * Interface provides a method that decides whether a class should be
 * transformed during runtime or not.
 *
 * @author David Schuler
 *
 */
public interface MutationDecision {

	/**
	 * Decides whether a class should be transformed.
	 * @param classNameWithDots
	 *            class name with dots as separator.
	 * @return True, if the class should be transformed
	 */
	public boolean shouldBeHandled(String classNameWithDots);
}
