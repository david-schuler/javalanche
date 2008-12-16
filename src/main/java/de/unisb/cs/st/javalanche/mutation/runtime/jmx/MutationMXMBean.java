package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

public interface MutationMXMBean {

	public int getNumberOfMutations();

	public String getMutations();

	public String getCurrentTest();

}
