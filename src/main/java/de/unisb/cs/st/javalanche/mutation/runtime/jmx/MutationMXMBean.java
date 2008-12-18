package de.unisb.cs.st.javalanche.mutation.runtime.jmx;


public interface MutationMXMBean {

	public int getNumberOfMutations();

	public String getMutations();

	public String getCurrentTest();

	public String getCurrentMutation();

	public String getMutationSummary();

	public long getMutationDuration();

	public long getTestDuration();

}
