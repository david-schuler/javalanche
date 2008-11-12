package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;


public class MutationDriverShutdownHook implements Runnable {

	private final MutationTestDriver mtd;

	public MutationDriverShutdownHook(MutationTestDriver mtd) {
		this.mtd = mtd;
	}

	public void run() {
		mtd.unexpectedShutdown();
	}

}
