package org.softevo.mutation.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestMessage {

	@Id
	@GeneratedValue
	private Long id;

	private String testCaseName;

	private String message;

	public TestMessage(String testCaseName, String message) {
		super();
		this.testCaseName = testCaseName;
		this.message = message;
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
		return String.format("TestCase: %s Reason: %s",testCaseName, message);
	}
}
