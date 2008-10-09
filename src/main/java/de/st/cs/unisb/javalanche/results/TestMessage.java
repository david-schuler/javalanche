package de.st.cs.unisb.javalanche.results;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.log4j.Logger;

/**
 *
 * Class that holds the result of one single TestCase.
 *
 * @author David Schuler
 *
 */
@Entity
public class TestMessage  implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final int MAX_MESSAGE_LENGTH = 65000;

	private static Logger logger = Logger.getLogger(TestMessage.class);

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
	@Lob
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

	public TestMessage(TestMessage testMessage) {
		this.id = new Long(0);
		this.testCaseName = testMessage.testCaseName;
		this.hasTouched = testMessage.hasTouched;
		this.message = testMessage.message;
		this.duration = 0;
		// this.duration = testMessage.duration;
	}

	public TestMessage(String testCaseName, String message, long duration) {
		super();
		this.testCaseName = testCaseName;
		if (message.length() > MAX_MESSAGE_LENGTH) {
			logger.info("Got long error message from test:  ("
					+ message.length() + ") " + testCaseName + "\n" + message);
			this.message = message.substring(0, Math.min(message.length(),
					MAX_MESSAGE_LENGTH));
		} else {
			this.message = message;
		}
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

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (duration ^ (duration >>> 32));
		result = PRIME * result + (hasTouched ? 1231 : 1237);
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		result = PRIME * result + ((message == null) ? 0 : message.hashCode());
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
		final TestMessage other = (TestMessage) obj;
		if (duration != other.duration)
			return false;
		if (hasTouched != other.hasTouched)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (testCaseName == null) {
			if (other.testCaseName != null)
				return false;
		} else if (!testCaseName.equals(other.testCaseName))
			return false;
		return true;
	}

	public boolean equalsWithoutID(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TestMessage other = (TestMessage) obj;
		if (duration != other.duration)
			return false;
		if (hasTouched != other.hasTouched)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (testCaseName == null) {
			if (other.testCaseName != null)
				return false;
		} else if (!testCaseName.equals(other.testCaseName))
			return false;
		return true;
	}
}
