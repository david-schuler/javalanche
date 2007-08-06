package org.softevo.mutation.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Mutation {

	@Id
	@GeneratedValue
	private Long id;

	private static final String PREFIX = "mutationId";

	static int mutionIdGenerator  = 0;

	public enum MutationType {
		REPLACE_INTEGER_CONSTANT, RIC_PLUS_1, RIC_MINUS_1, RIC_ZERO
	};

	private String className;

	private int line;

	private MutationType mutation;

	private int mutionId;

	private Mutation(){

	}

	public Mutation(String className, int line, MutationType mutation) {
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

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


}
