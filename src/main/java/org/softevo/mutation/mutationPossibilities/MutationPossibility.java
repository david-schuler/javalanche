package org.softevo.mutation.mutationPossibilities;

public class MutationPossibility {

	public enum Mutation {
		REPLACE_INTEGER_CONSTANT
	};

	private String className;

	private int line;

	private Mutation mutation;

	public MutationPossibility(String className, int line, Mutation mutation) {
		super();
		if (className == null || line < 0 || mutation == null) {
			throw new IllegalArgumentException(String.format(
					"Arguments were: %s - %d - %s", className, line, mutation));
		}

		this.className = className;
		this.line = line;
		this.mutation = mutation;
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

}
