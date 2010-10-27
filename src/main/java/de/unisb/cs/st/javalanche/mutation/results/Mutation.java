/*
 * Copyright (C) 2009 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.results;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * Class that stores a mutation. It stores its type, where it is (was) applied
 * and the results of the tests.
 * 
 * @author David Schuler
 * 
 */
/**
 * @author David Schuler
 * 
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "className",
		"lineNumber", "mutationForLine", "mutationType" }) })
public class Mutation implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private static final String PREFIX = "mutationId";

	static int mutionIdGenerator = 0;

	public enum MutationType {
		NO_MUTATION("No mutation"), RIC_PLUS_1("Constant +1"), RIC_MINUS_1(
				"Constant -1"), RIC_ZERO("Replace Constant by 0"), NEGATE_JUMP(
				"Negate jump condition"), ARITHMETIC_REPLACE(
				"Replace arithmetic operator"), REMOVE_CALL(
				"Remove method call"),

		ADAPTED_JUMP("adapted jump"), ADAPTED_SKIP_ELSE("Skip else block"), ADAPTED_REMOVE_CHECK(
				"Always executed if block"), ADAPTED_SKIP_IF("Skip if block"), ADAPTED_NEGATE_JUMP_IN_IF(
				"Negate Jump in if statement only"), ADAPTED_ALWAYS_ELSE(
				"Always execute else block"), ADAPTED_REPLACE(
				"Replace variable with default value"), ADAPTED_REPLACE_STORE(
				"Replace store to variable with default value"), ADAPTED_REPLACE_METHOD_ARG(
				"Replace argument of a method call"), ADAPTED_REPLACE_ASSIGNMENT(
				"Replace variable assignment"), ADAPTED_REPLACE_FIELD(
				"Replace field assignemt"), ADAPTED_REPLACE_RETURN(
				"Replace variable in return");

		private String desc;

		MutationType(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}

	};

	/**
	 * Name of the class that contains the mutation.
	 */
	private String className;

	/**
	 * Name of the method that contains the mutation.
	 */
	@Column(length = 1000)
	private String methodName;

	/**
	 * The line number of the mutation, -1 is used when no line number is
	 * available.
	 */
	private int lineNumber;

	/**
	 * When more than one mutation of the same type can be applied for one line
	 * this field is used. It starts with 0.
	 */
	private int mutationForLine;

	/**
	 * Type of the mutation.
	 */
	private MutationType mutationType;

	/**
	 * Result for applying this mutation.
	 */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private MutationTestResult mutationResult;

	private String addInfo;

	/**
	 * Default constructor needed by Hibernate.
	 */
	@SuppressWarnings("unused")
	private Mutation() {
	}

	public Mutation(String className, String methodName, int line,
			int mutationForLine, MutationType mutation) {
		super();
		if (className == null || methodName == null || line < -1
				|| mutation == null || mutationForLine < 0) {
			throw new IllegalArgumentException(String.format(
					"Arguments were: %s, %s, %d, %d, %s", className,
					methodName, line, mutationForLine, mutation));
		}

		if (className.contains("/")) {
			className = className.replace('/', '.');
		}
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = line;
		this.mutationForLine = mutationForLine;
		this.mutationType = mutation;
	}

	@Deprecated
	public Mutation(String className, int line, int mutationForLine,
			MutationType mutation, boolean classInit) {
		this(className, null, line, mutationForLine, mutation);
	}

	@Override
	public String toString() {
		return String
				.format(
						"%d %s - %s - %d (%d)- %s \n%s",
						id,
						className,
						methodName,
						/* isClassInit() ? "in static part" : "not static", */lineNumber,
						mutationForLine, mutationType.toString(),
						mutationResult == null ? "No Result" : mutationResult
								.toString());
	}

	public String toShortString() {
		return String.format("%d %s - %s - %d (%d)- %s  [%s]", id, className,
				methodName, lineNumber, mutationForLine, mutationType
						.toString(), mutationResult == null ? "No Result"
						: mutationResult.toShortString());

	}

	public String getClassName() {
		return className;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMutationVariable() {
		return PREFIX + "_" + id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 * @return the id
	 */
	public Long setId(Long id) {
		return this.id = id;
	}

	/**
	 * @return the mutationType
	 */
	public MutationType getMutationType() {
		return mutationType;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param lineNumber
	 *            the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @param mutationType
	 *            the mutationType to set
	 */
	public void setMutationType(MutationType mutationType) {
		this.mutationType = mutationType;
	}

	/**
	 * @return the mutationResult
	 */
	public MutationTestResult getMutationResult() {
		return mutationResult;
	}

	/**
	 * @param mutationTestResult
	 *            the mutationResult to set
	 */
	public void setMutationResult(MutationTestResult mutationTestResult) {
		this.mutationResult = mutationTestResult;
	}

	/**
	 * @return the mutationForLine
	 */
	public int getMutationForLine() {
		return mutationForLine;
	}

	/**
	 * @param mutationForLine
	 *            the mutationForLine to set
	 */
	public void setMutationForLine(int mutationForLine) {
		this.mutationForLine = mutationForLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((className == null) ? 0 : className.hashCode());
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		result = PRIME * result + lineNumber;
		result = PRIME * result + mutationForLine;
		result = PRIME * result
				+ ((mutationResult == null) ? 0 : mutationResult.hashCode());
		result = PRIME * result
				+ ((mutationType == null) ? 0 : mutationType.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Mutation other = (Mutation) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (mutationForLine != other.mutationForLine)
			return false;
		if (mutationResult == null) {
			if (other.mutationResult != null)
				return false;
		} else if (!mutationResult.equals(other.mutationResult))
			return false;
		if (mutationType == null) {
			if (other.mutationType != null)
				return false;
		} else if (!mutationType.equals(other.mutationType))
			return false;
		return true;
	}

	/**
	 * Same behavior as equals without checking for equal ids, or equal mutation
	 * results.
	 * 
	 * @param comp
	 *            Mutation to compare.
	 * @return True, if both objects are equal except of their IDs and results.
	 */
	public boolean equalsWithoutIdAndResult(Mutation comp) {
		if (this == comp)
			return true;
		if (comp == null)
			return false;
		if (getClass() != comp.getClass())
			return false;
		final Mutation other = (Mutation) comp;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (mutationForLine != other.mutationForLine)
			return false;
		if (mutationType == null) {
			if (other.mutationType != null)
				return false;
		} else if (!mutationType.equals(other.mutationType))
			return false;
		return true;
	}

	public boolean isKilled() {
		return mutationResult != null
				&& (mutationResult.getNumberOfErrors() > 0 || mutationResult
						.getNumberOfFailures() > 0);
	}

	public void loadAll() {
		if (mutationResult != null) {
			mutationResult.loadAll();
		}
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public static String getCsvHead() {
		return "ID,CLASS_NAME,METHOD_NAME,LINE_NUMBER,MUTATION_FOR_LINE,MUTATION_TYPE";
	}

	public String getCsvString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(',');
		sb.append(className);
		sb.append(',');
		sb.append(methodName);
		sb.append(',');
		sb.append(lineNumber);
		sb.append(',');
		sb.append(mutationForLine);
		sb.append(',');
		sb.append(mutationType.toString());
		return sb.toString();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
