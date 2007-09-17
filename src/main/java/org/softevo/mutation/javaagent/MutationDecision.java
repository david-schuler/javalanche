package org.softevo.mutation.javaagent;

public interface MutationDecision {

	public boolean shouldBeScanned(String classNameWithDots);
}
