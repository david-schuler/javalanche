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
package de.unisb.cs.st.javalanche.mutation.properties;

public enum RunMode {
	SCAN("scan"), MUTATION_TEST("mutation"), CHECK_TESTS("check-tests"), TEST_PERMUTED(
			"check-tests-permuted"), MUTATION_TEST_INVARIANT(
			"mutation-invariant"), MUTATION_TEST_COVERAGE("mutation-coverage"), CREATE_COVERAGE_MULT(
			"create-coverage-mult"), OFF("off"), SCAN_PROJECT("scan-project");

	private String key;

	RunMode(String key) {
		this.key = key;
	}

	/**
	 * Returns a key for the RunMode. The key is usually specified at the
	 * command line.
	 * 
	 * @return the key for the run mode.
	 */
	public String getKey() {
		return key;
	}

}
