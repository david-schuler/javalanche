package org.softevo.mutation.results;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.softevo.mutation.testsuite.MutationTestListener;

import junit.framework.TestResult;

@Entity
public class SingleTestResult {

	@Id
	@GeneratedValue
	private Long id;

	private int runs = -1;

//	@OneToMany(cascade = CascadeType.ALL)
	@Transient
	private List<TestMessage> failures  = new ArrayList<TestMessage>();

//	@OneToMany(cascade = CascadeType.ALL)
	@Transient
	private List<TestMessage> errors = new ArrayList<TestMessage>();

	private SingleTestResult() {
	}

	public SingleTestResult(TestResult mutationTestResult,
			MutationTestListener mutationTestListener) {
		this.runs = mutationTestResult.runCount();
		this.failures = mutationTestListener.getFailureMessages();
		this.errors = mutationTestListener.getErrorMessages();

	}

	@Override
	public String toString() {
		return String.format("Runs: %d  Failures: %d  Errors: %d", runs,
				failures.size(), errors.size());
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
	public int getNumberOfErrors() {
		return errors.size();
	}

	/**
	 * @return the failures
	 */
	public int getNumberOfFailures() {
		return failures.size();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the errors
	 */
	public List<TestMessage> getErrors() {
		return errors;
	}

	/**
	 * @return the failures
	 */
	public List<TestMessage> getFailures() {
		return failures;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<TestMessage> errors) {
		this.errors = errors;
	}

	/**
	 * @param failures
	 *            the failures to set
	 */
	public void setFailures(List<TestMessage> failures) {
		this.failures = failures;
	}

}