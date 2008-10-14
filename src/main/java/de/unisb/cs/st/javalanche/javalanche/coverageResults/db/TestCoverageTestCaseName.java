package de.unisb.cs.st.javalanche.mutation.coverageResults.db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestCoverageTestCaseName {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String testCaseName;

	private static Map<String, TestCoverageTestCaseName> instances = new HashMap<String, TestCoverageTestCaseName>();

	public static TestCoverageTestCaseName getTestCoverageTestCaseName(
			String testCaseName) {
		if (instances.containsKey(testCaseName)) {
			return instances.get(testCaseName);
		}
		TestCoverageTestCaseName testCoverageTestCaseName=  new TestCoverageTestCaseName(testCaseName);
		instances.put(testCaseName,testCoverageTestCaseName);
		return testCoverageTestCaseName;
	}

	public TestCoverageTestCaseName() {
		super();
	}

	private TestCoverageTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the testCaseName
	 */
	public String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * @param testCaseName
	 *            the testCaseName to set
	 */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
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
				+ ((testCaseName == null) ? 0 : testCaseName.hashCode());
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
		final TestCoverageTestCaseName other = (TestCoverageTestCaseName) obj;
		if (testCaseName == null) {
			if (other.testCaseName != null)
				return false;
		} else if (!testCaseName.equals(other.testCaseName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return testCaseName;
	}

}
