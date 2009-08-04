package de.unisb.cs.st.javalanche.rhino.coverage;

public class PriorizationResult {

	private String testName;

	private String info;

	public PriorizationResult(String className, String info) {
		super();
		this.testName = className;
		this.info = info;
	}

	/**
	 * @return the className
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

}
