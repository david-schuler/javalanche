package org.softevo.mutation.runtime;

/**
 * Class that holds the result for one run of the program. Needed when run in
 * several JVMs.
 *
 * @author David Schuler
 *
 */
public class RunResult {

	private int mutations;

	private int touched;

	/**
	 * @return the mutations the number of for which results are stored.
	 */
	public int getMutations() {
		return mutations;
	}

	/**
	 * @return the number touched mutation
	 */
	public int getTouched() {
		return touched;
	}

	public RunResult(int mutations, int touched) {
		super();
		this.mutations = mutations;
		this.touched = touched;
	}

	@Override
	public String toString() {
		return String
				.format(
						"%d Mutation Results were recorded\n%d Mutations where actually touched",
						mutations, touched);
	}
}
