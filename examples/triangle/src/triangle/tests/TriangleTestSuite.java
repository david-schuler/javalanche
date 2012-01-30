package triangle.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TriangleTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(Triangle1Test.class);
		suite.addTestSuite(Triangle2Test.class);
		suite.addTestSuite(Triangle3Test.class);
		return suite;
	}
}
