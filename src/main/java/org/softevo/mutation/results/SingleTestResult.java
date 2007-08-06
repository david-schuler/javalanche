package org.softevo.mutation.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import junit.framework.TestResult;

@Entity
public class SingleTestResult {

	@Id
	@GeneratedValue
	private Long id;

	private int runs;

	private int failures;

	private int errors;

	private SingleTestResult() {
	}

	public SingleTestResult(int runs, int failures, int errors) {
		super();
		this.runs = runs;
		this.failures = failures;
		this.errors = errors;
	}

	public SingleTestResult(TestResult testResult) {
		super();
		this.runs = testResult.runCount();
		this.failures = testResult.failureCount();
		this.errors = testResult.errorCount();
	}

	@Override
	public String toString() {
		return String.format("Runs: %d  Failures: %d  Errors: %d", runs,
				failures, errors);
	}

	/**
	 * @return the runs
	 */
	public int getRuns() {
		return runs;
	}

	/**
	 * @param runs
	 *            the runs to set
	 */
	public void setRuns(int runs) {
		this.runs = runs;
	}

	/**
	 * @return the errors
	 */
	public int getErrors() {
		return errors;
	}

	/**
	 * @return the failures
	 */
	public int getFailures() {
		return failures;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(int errors) {
		this.errors = errors;
	}

	/**
	 * @param failures
	 *            the failures to set
	 */
	public void setFailures(int failures) {
		this.failures = failures;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}