package org.softevo.mutation.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import junit.framework.TestResult;

import org.hibernate.annotations.IndexColumn;
import org.softevo.mutation.testsuite.MutationTestListener;

@Entity
public class SingleTestResult {

	@Id
	@GeneratedValue
	private Long id;

	private int runs;

	/**
	 * True if the mutation was touched by at least one TestCase;
	 */
	boolean touched;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("testCaseName")
	@IndexColumn(name = "failure_list_id")
	private List<TestMessage> failures = new ArrayList<TestMessage>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "SingleTestResult_Errors", joinColumns = { @JoinColumn(name = "singleTestResult_id") }, inverseJoinColumns = @JoinColumn(name = "testMessage_id"))
	@IndexColumn(name="error_id")
	private List<TestMessage> errors = new ArrayList<TestMessage>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "SingleTestResult_Passing", joinColumns = { @JoinColumn(name = "singleTestResult_id") }, inverseJoinColumns = @JoinColumn(name = "testMessage_id"))
	@IndexColumn(name="passing_id")
	private List<TestMessage> passing = new ArrayList<TestMessage>();

	private SingleTestResult() {
	}

	public SingleTestResult(TestResult mutationTestResult,
			MutationTestListener mutationTestListener,
			Set<String> touchingTestCases) {
		this.runs = mutationTestResult.runCount();
		this.failures = mutationTestListener.getFailureMessages();
		this.errors = mutationTestListener.getErrorMessages();
		this.passing = mutationTestListener.getPassingMessages();
		if (touchingTestCases != null && touchingTestCases.size() > 0) {
			updateTouched(touchingTestCases, failures);
			updateTouched(touchingTestCases, errors);
			updateTouched(touchingTestCases, passing);
//			updateTimes(mutationTestListener.getDurations());
			touched = true;
		}
	}



	private static void updateTouched(Set<String> touchingTestCases,
			List<TestMessage> testMessages) {
		for (TestMessage tm : testMessages) {
			if (touchingTestCases.contains(tm.getTestCaseName())) {
				tm.setHasTouched(true);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format(
				"Runs: %d  Failures: %d  Errors: %d LineTouched: %s", runs,
				failures.size(), errors.size(), touched ? "yes" : "no "));
		if (failures.size() > 0) {
			sb.append("Failures:\n");
			for (TestMessage tm : failures) {
				sb.append(tm);
				sb.append('\n');
			}
		}
		if (errors.size() > 0) {
			sb.append("Errors:\n");
			for (TestMessage tm : errors) {
				sb.append(tm);
				sb.append('\n');
			}
		}
		if (passing.size() > 0) {
			sb.append("Passing:\n");
			for (TestMessage tm : passing) {
				sb.append(tm);
				sb.append('\n');
			}
		}
		return sb.toString();

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
	public Collection<TestMessage> getErrors() {
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

	/**
	 * @return the passing
	 */
	public List<TestMessage> getPassing() {
		return passing;
	}

	/**
	 * @param passing
	 *            the passing to set
	 */
	public void setPassing(List<TestMessage> passing) {
		this.passing = passing;
	}

	/**
	 * @return the touched
	 */
	public boolean isTouched() {
		return touched;
	}

	/**
	 * @param touched
	 *            the touched to set
	 */
	public void setTouched(boolean touched) {
		this.touched = touched;
	}

}