package org.softevo.mutation.javaagent;

public interface MutationDecision {

	public boolean shouldBeHandled(String classNameWithDots);
}
