package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

public interface MutationMXMBean {

	public int getConductedMutations();

	public void setNextSafe(boolean b);

	public boolean getNextSafe();
}
