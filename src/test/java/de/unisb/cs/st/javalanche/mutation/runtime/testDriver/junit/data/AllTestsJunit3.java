package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import org.junit.Ignore;

import junit.framework.Test;
import junit.framework.TestSuite;
import static de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.DebugTestClass.TestType.*;

@Ignore
public class AllTestsJunit3 {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTestsJunit3.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(new DebugTestClass(1, 2, PASS));
		suite.addTest(new DebugTestClass(2, 2, FAIL));
		suite.addTest(new DebugTestClass(3, 2, ERROR));
		// $JUnit-END$
		return suite;
	}

}
