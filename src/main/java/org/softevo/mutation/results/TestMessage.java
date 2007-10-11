package org.softevo.mutation.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * Class that holds the result of one single TestCase.
 *
 * @author David Schuler
 *
 */
@Entity
public class TestMessage {

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * name of the TestCases
	 */
	private String testCaseName;

	/**
	 * Failure or error message of the TestCase.
	 */
	private String message;

	/**
	 * True, if test case has touched the mutation.
	 */
	private boolean hasTouched;

	/**
	 * Time a testCase needed to execute.
	 */
	private long duration;

	public TestMessage(String testCaseName, String message) {
		this(testCaseName, message, 0);

	}

	public TestMessage(String testCaseName, String message, long duration) {
		super();
		this.testCaseName = testCaseName;
		this.message = message.substring(0, Math.min(message.length(), 254));
		this.duration = duration;
	}

	public TestMessage() {
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the testCaseName
	 */
	public String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param testCaseName
	 *            the testCaseName to set
	 */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	@Override
	public String toString() {
		return String.format("TestCase: %s Touched: %s Reason: %s",
				testCaseName, hasTouched ? "yes" : "no", message);
	}

	/**
	 * @return the hasTouched
	 */
	public boolean isHasTouched() {
		return hasTouched;
	}

	/**
	 * @param hasTouched
	 *            the hasTouched to set
	 */
	public void setHasTouched(boolean hasTouched) {
		this.hasTouched = hasTouched;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
}
