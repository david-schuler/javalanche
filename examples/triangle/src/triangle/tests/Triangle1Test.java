package triangle.tests;

import triangle.Triangle;
import triangle.TriangleType;
import static triangle.TriangleType.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Triangle1Test extends TestCase {

	public void test1() {
		TriangleType type = Triangle.classify(1, 2, 3);
		assertEquals(SCALENE, type);
	}

	// public void testInvalid1() {
	// TriangleType type = Triangle.classify(1, 2, 4);
	// assertEquals(INVALID, type);
	// }
	//
	// public void testInvalid2() {
	// TriangleType type = Triangle.classify(1, 4, 2);
	// assertEquals(INVALID, type);
	// }

	public void testInvalid3() {
		TriangleType type = Triangle.classify(4, 1, 2);
		assertEquals(INVALID, type);
	}

	//
	// public void testInvalidNeg1() {
	// TriangleType type = Triangle.classify(-1, 1, 1);
	// assertEquals(INVALID, type);
	// }

	// public void testInvalidNeg2() {
	// TriangleType type = Triangle.classify(1, -1, 1);
	// assertEquals(INVALID, type);
	// }
	//
	public void testInvalidNeg3() {
		TriangleType type = Triangle.classify(1, 1, -1);
		assertEquals(INVALID, type);
	}

	public void testEquiliteral() {
		TriangleType type = Triangle.classify(1, 1, 1);
		assertEquals(EQUILATERAL, type);
	}

	public void testIsoceles1() {
		TriangleType type = Triangle.classify(2, 2, 3);
		assertEquals(ISOSCELES, type);
	}

	// public void testIsoceles2() {
	// TriangleType type = Triangle.classify(2, 3, 2);
	// assertEquals(ISOSCELES, type);
	// }
	//
	// public void testIsoceles3() {
	// TriangleType type = Triangle.classify(3, 2, 2);
	// assertEquals(ISOSCELES, type);
	// }
	//
	public void testInvalid() {
		TriangleType type = Triangle.classify(3, 1, 1);
		assertEquals(INVALID, type);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(Triangle1Test.class);
		return suite;
	}
}
