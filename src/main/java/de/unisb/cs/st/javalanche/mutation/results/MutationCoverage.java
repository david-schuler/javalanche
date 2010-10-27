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
package de.unisb.cs.st.javalanche.mutation.results;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class MutationCoverage {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private Long mutationID;

	@ManyToMany
	// , fetch = FetchType.EAGER)
	// @IndexColumn(name = "coverage_test_names")
	// @JoinColumn(name="TESTNAME_ID")
	private List<TestName> testNames = new ArrayList<TestName>();

	public MutationCoverage() {
	}

	public MutationCoverage(Long mutationID, List<TestName> testNames) {
		super();
		this.mutationID = mutationID;
		this.testNames = testNames;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the mutationID
	 */
	public Long getMutationID() {
		return mutationID;
	}

	/**
	 * @return the testsNames
	 */
	public List<TestName> getTestsNames() {
		return testNames;
	}

	/**
	 * @param mutationID
	 *            the mutationID to set
	 */
	public void setMutationID(Long mutationID) {
		this.mutationID = mutationID;
	}

	/**
	 * @param testNames
	 *            the testsNames to set
	 */
	public void setTestsNames(List<TestName> testNames) {
		this.testNames = testNames;
	}

	public void addTestName(TestName testName) {
		testNames.add(testName);
	}

	public void addTestNames(List<TestName> testNames) {
		this.testNames.addAll(testNames);

	}

}
