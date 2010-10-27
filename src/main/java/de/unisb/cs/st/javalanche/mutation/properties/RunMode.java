/*
 * Copyright (C) 2010 Saarland University
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
	SCAN("scan"), MUTATION_TEST("mutation"), CHECK_TESTS("test1"), MUTATION_TEST_INVARIANT(
			"mutation-invariant"), MUTATION_TEST_INVARIANT_PER_TEST(
			"mutation-invariant-per-test"), MUTATION_TEST_COVERAGE(
			"mutation-coverage"), CREATE_COVERAGE("create-coverage"), OFF("off"), CHECK_INVARIANTS_PER_TEST(
			"check-per-test"), TEST_PERMUTED("test3"), SCAN_PROJECT(
			"scan-project"), SCAN_ECLIPSE("scan-eclipse"), EVOLUTION(
			"evolution");

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
