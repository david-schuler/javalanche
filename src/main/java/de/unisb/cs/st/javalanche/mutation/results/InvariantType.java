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