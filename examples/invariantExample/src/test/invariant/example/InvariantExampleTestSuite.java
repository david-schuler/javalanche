package invariant.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class InvariantExampleTestSuite extends TestCase {

	public void testMethod1() {
		int method1 = new Main().method1();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(InvariantExampleTestSuite.class);
		return suite;
	}

}
