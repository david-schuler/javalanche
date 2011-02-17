package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class DebugTestClass extends TestCase {

	public enum TestType {
		PASS("testPass"), FAIL("testFail"), ERROR("testError");

		private String test;

		private TestType(String test) {
			this.test = test;
		}

		public String getTest() {
			return test;
		}
	}

	public static final String ERROR_MESSAGE = "Error message: adrgadkljfgvsmdf";
	public static final String FAILURE_MESSAGE = "Failure message: kjgrknv,zxcv.dfg|";

	public DebugTestClass(int iterations, int mode, TestType tt) {
		super(tt.getTest());
		// "debugtest" + iterations + " " + mode + " " + seed
	}

	public void testPass() {
		System.out.println("DebugTestClass.testPass()");
	}

	public void testFail() {
		System.out.println("DebugTestClass.testFail()");
		fail(FAILURE_MESSAGE);
	}

	public void testError() {
		System.out.println("DebugTestClass.testError()");
		throw new NullPointerException(ERROR_MESSAGE);
	}

}
