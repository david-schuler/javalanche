package de.unisb.cs.st.javalanche.mutation.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;

/**
 *
 * Class that stores an invariant. It stores its type, where it holds.
 *
 * @author David Schuler
 *
 */
@Entity
public class Invariant {

	@Id
	@GeneratedValue
	private Long id;

	private int checkerId;

	private InvariantType type;

	protected String className;

	protected String methodName;

	protected String methodDescriptor;

	private String[] variableNames;

	private String[] variableTypes;

	private boolean preOrPost;

	private boolean handled = true;

	/**
	 * Default constructor needed by Hibernate.
	 */
	@SuppressWarnings("unused")
	private Invariant() {
	}

	public Invariant(InvariantChecker invariantChecker) {
		type = getType(invariantChecker);
		className = invariantChecker.getClassName();
		methodName = invariantChecker.getMethodName();
		methodDescriptor = invariantChecker.getDesc();
		variableNames = invariantChecker.getVariableNames();
		assert type.getParameters() == variableNames.length;
		variableTypes = invariantChecker.getTypes();
		preOrPost = invariantChecker.isPre();
		handled = invariantChecker.isHandled();
		checkerId = invariantChecker.getId();
	}

	private static InvariantType getType(InvariantChecker invariantChecker) {
		for (InvariantType type : InvariantType.values()) {
			if (type.getInvariantClass().equals(invariantChecker.getClass())) {
				return type;
			}
		}
		throw new RuntimeException("Did not find type for " + invariantChecker);
	}

	/**
	 * @return the methodDescriptor
	 */
	public String getMethodDescriptor() {
		return methodDescriptor;
	}

	/**
	 * @param methodDescriptor
	 *            the methodDescriptor to set
	 */
	public void setMethodDescriptor(String methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DB id: " + id);
		sb.append("Invariant id: " + checkerId);
		sb.append(" - " + type);
		sb.append(preOrPost ? " pre " : " post ");
		sb.append(" - " + className);
		sb.append(" - " + methodName);
		sb.append(" - " + methodDescriptor);
		sb.append(" Variable names: ");
		for (String variableName : variableNames) {
			sb.append(variableName + "  ");
		}
		sb.append(" Variable types: ");
		for (String variableType : variableTypes) {
			sb.append(variableType + "  ");
		}
		return sb.toString();
	}
}