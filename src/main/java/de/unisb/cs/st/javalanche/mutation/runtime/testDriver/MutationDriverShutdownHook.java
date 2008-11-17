package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

/**
 * Shutdown hook that is used during mutation testing. In case of an unexpected
 * shutdown this hook is activated. Most likely this will be an endless loop
 * caused by a mutation.
 *
 * @author David Schuler
 *
 */
public class MutationDriverShutdownHook implements Runnable {

	private final MutationTestDriver mtd;

	/**
	 * Creates a knew ShutdownHook for the given {@link MutationTestDriver}.
	 *
	 * @param mtd
	 *            the mutation test dirver the shutdown hook is created for
	 */
	public MutationDriverShutdownHook(MutationTestDriver mtd) {
		this.mtd = mtd;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		mtd.unexpectedShutdown();
	}

}
