/*
 * Copyright (C) 2011 Saarland University
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
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "className",
		"lineNumber", "mutationForLine", "mutationType", "operatorAddInfo" }) })
public class Mutation implements Serializable, Comparable<Mutation> {

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
		NO_MUTATION("No mutation"), REPLACE_CONSTANT("Replace a constant"), NEGATE_JUMP(
				"Negate jump condition"), ARITHMETIC_REPLACE(
				"Replace arithmetic operator"), REMOVE_CALL(
				"Remove method call"), REPLACE_VARIABLE(
				"Replace variable reference"), ABSOLUTE_VALUE(
				"Insert absolute value of a variable"),
				UNARY_OPERATOR("Insert unary operator"),
				
				/* Concurrent Operators start*/
				REPLACE_THREAD_CALL("Replace thread method call (join/sleep/lock/unlock)"),
				MONITOR_REMOVE("Remove monitor enter/exit")
				/* Concurrent Operators end*/
				;
;

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

	/**
	 * Additional string representation of the operator.
	 */
	private String addInfo; //TODO rename to description

	/**
	 * Additional information for the operator that is used internally.
	 */
	private String operatorAddInfo;

	private Long baseMutationId;

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
				.format("%d %s - %s - %d (%d) - %s%s%s\n%s",
						id,
						className,
						methodName,
						/* isClassInit() ? "in static part" : "not static", */lineNumber,
						mutationForLine,
						mutationType.toString(),
						operatorAddInfo == null ? "" : "(" + operatorAddInfo
								+ ")",
						addInfo == null ? "" : " - " + addInfo,
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
		if (operatorAddInfo == null) {
			if (other.operatorAddInfo != null)
				return false;
		} else if (!operatorAddInfo.equals(other.operatorAddInfo))
			return false;
		return true;
	}

	@Deprecated
	public boolean isKilled() {
		return isDetected();
	}

	public boolean isDetected() {
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
		return "ID,CLASS_NAME,METHOD_NAME,LINE_NUMBER,MUTATION_FOR_LINE,MUTATION_TYPE,OPERATOR_ADD_INFO,ADD_INFO";
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
		sb.append(',');
		sb.append(operatorAddInfo);
		sb.append(',');
		sb.append(addInfo);
		return sb.toString();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int compareTo(Mutation o) {
		if (this.equals(o)) {
			return 0;
		}
		if (o == null) {
			return 1;
		}
		int comp = getClassName().compareTo(o.getClassName());
		if (comp != 0) {
			return comp;
		}
		if (getLineNumber() != o.getLineNumber()) {
			return (getLineNumber() < o.getLineNumber() ? -1 : 1);
		}
		if (getMutationType() != o.getMutationType()) {
			return getMutationType().compareTo(o.getMutationType());
		}
		if (getMutationForLine() != o.getMutationForLine()) {
			return (getMutationForLine() < o.getMutationForLine() ? -1 : 1);
		}
		if (getOperatorAddInfo() != null
				&& getOperatorAddInfo().compareTo(o.getOperatorAddInfo()) != 0) {
			return getOperatorAddInfo().compareTo(o.getOperatorAddInfo());
		}
		return 0;
	}

	public String getOperatorAddInfo() {
		return operatorAddInfo;
	}

	public void setOperatorAddInfo(String operatorAddInfo) {
		this.operatorAddInfo = operatorAddInfo;
	}

	public void setBaseMutationId(Long baseMutationId) {
		this.baseMutationId = baseMutationId;
	}

	public Long getBaseMutationId() {
		return baseMutationId;
	}

	/**
	 * Copies the mutation but does not copy the additional info fields.
	 * @param m the mutation to copy
	 * @return a copy of the given mutation.
	 */
	public static Mutation copyMutation(Mutation m){
		return new Mutation(m.getClassName(),m.getMethodName(),m.getLineNumber(),m.getMutationForLine(),m.getMutationType());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addInfo == null) ? 0 : addInfo.hashCode());
		result = prime * result
				+ ((baseMutationId == null) ? 0 : baseMutationId.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + lineNumber;
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + mutationForLine;
		result = prime * result
				+ ((mutationResult == null) ? 0 : mutationResult.hashCode());
		result = prime * result
				+ ((mutationType == null) ? 0 : mutationType.hashCode());
		result = prime * result
				+ ((operatorAddInfo == null) ? 0 : operatorAddInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mutation other = (Mutation) obj;
		if (addInfo == null) {
			if (other.addInfo != null)
				return false;
		} else if (!addInfo.equals(other.addInfo))
			return false;
		if (baseMutationId == null) {
			if (other.baseMutationId != null)
				return false;
		} else if (!baseMutationId.equals(other.baseMutationId))
			return false;
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
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (mutationForLine != other.mutationForLine)
			return false;
		if (mutationResult == null) {
			if (other.mutationResult != null)
				return false;
		} else if (!mutationResult.equals(other.mutationResult))
			return false;
		if (mutationType != other.mutationType)
			return false;
		if (operatorAddInfo == null) {
			if (other.operatorAddInfo != null)
				return false;
		} else if (!operatorAddInfo.equals(other.operatorAddInfo))
			return false;
		return true;
	}
}
