package de.unisb.cs.st.javalanche.mutation.coverageResults.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity
public class TestCoverageLineResult {

	@Id
	@GeneratedValue
	private Long id;

	private int lineNumber;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<TestCoverageTestCaseName> testCases;


	public TestCoverageLineResult() {
		super();
	}

	public TestCoverageLineResult(int lineNumber, List<String> testCases) {
		super();
		this.lineNumber = lineNumber;
		this.testCases = convert(testCases);
	}

	private List<TestCoverageTestCaseName> convert(List<String> testCases2) {
		List<TestCoverageTestCaseName> result= new ArrayList<TestCoverageTestCaseName>();
		for (String string : testCases2) {
			result.add(TestCoverageTestCaseName.getTestCoverageTestCaseName(string));
		}
		return result;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return the testCases
	 */
	public List<TestCoverageTestCaseName> getTestCases() {
		return testCases;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @param testCases the testCases to set
	 */
	public void setTestCases(List<TestCoverageTestCaseName> testCases) {
		this.testCases = testCases;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder db = new StringBuilder();
		db.append(" line "+ lineNumber );
		db.append(" - Testcases: [");
		for(TestCoverageTestCaseName name : testCases){
			db.append(name);
			db.append(", ");
		}
		db.append(']');
		return db.toString();
	}
}
