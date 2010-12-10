package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import org.junit.Ignore;

import junit.framework.Test;
import junit.framework.TestSuite;

@Ignore
public class AllTestsJunit3 {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTestsJunit3.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(new DebugTestClass(1, 2, 3));
		// $JUnit-END$
		return suite;
	}

}
