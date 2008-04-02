package org.softevo.mutation.results;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class MutationCoverage {

	@Id
	@GeneratedValue
	private Long id;

	private Long mutationID;

	@ManyToMany(cascade = CascadeType.ALL)
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

	public MutationCoverage(Long key, ArrayList<String> arrayList) {
		this.mutationID = key;
		testNames = new ArrayList<TestName>();
		for (String name : arrayList) {
			testNames.add(new TestName(name));
		}
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

}
