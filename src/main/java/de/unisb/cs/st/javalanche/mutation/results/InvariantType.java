/**
 *
 */
package de.unisb.cs.st.javalanche.mutation.results;

import de.unisb.cs.st.javalanche.invariants.invariants.checkers.EqualInvariantChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.IntEqualChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.IntGreaterLessChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.LowerBoundChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.NonZeroChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.RangeIntChecker;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.UpperBoundChecker;

public enum InvariantType {

	VALUE_EQUAL(1, EqualInvariantChecker.class), INT_EQUAL(2,
			IntEqualChecker.class), INT_GREATER_LESS(2,
			IntGreaterLessChecker.class), NON_ZERO(1, NonZeroChecker.class), LOWER_BOUND(
			1, LowerBoundChecker.class), UPPER_BOUND(1,
			UpperBoundChecker.class), RANGE(1, RangeIntChecker.class);

	private final int parameters;
	private Class<? extends InvariantChecker> invariantClass;

	/**
	 * @param parameters
	 * @param cl
	 */
	InvariantType(int parameters, Class<? extends InvariantChecker> cl) {
		this.parameters = parameters;
		this.invariantClass = cl;
	}

	/**
	 * @return the parameters
	 */
	public int getParameters() {
		return parameters;
	}

	/**
	 * @return the invariantClass
	 */
	public Class<? extends InvariantChecker> getInvariantClass() {
		return invariantClass;
	}
}