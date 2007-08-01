package org.softevo.mutation.mutationPossibilities;

public class MutationPossibility {

	private static final String PREFIX = "mutationId";

	static int mutionIdGenerator  = 0;

	public enum Mutation {
		REPLACE_INTEGER_CONSTANT
	};

	private String className;

	private int line;

	private Mutation mutation;

	private int mutionId;

	public MutationPossibility(String className, int line, Mutation mutation) {
		super();
		if (className == null || line < 0 || mutation == null) {
			throw new IllegalArgumentException(String.format(
					"Arguments were: %s - %d - %s", className, line, mutation));
		}
		if (className.contains("/")) {
			className = className.replace('/', '.');
		}
		this.className = className;
		this.line = line;
		this.mutation = mutation;
		mutionId = mutionIdGenerator++;
	}

	@Override
	public String toString() {
		return String.format("%s - %d - %s", className, line, mutation
				.toString());
	}

	public String getClassName() {
		return className;
	}

	public int getLineNumber() {
		return line;
	}

	/**
	 * @return the mutionId
	 */
	public int getMutionId() {
		return mutionId;
	}

	public String getMutationVariable(){
		return PREFIX + "_" + mutionId;
	}


}
