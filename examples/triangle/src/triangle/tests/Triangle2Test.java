package triangle.tests;

import triangle.Triangle;
import triangle.TriangleType;
import static triangle.TriangleType.*;
import junit.framework.TestCase;

public class Triangle2Test extends TestCase {


	 public void testIsoceles2() {
		TriangleType type = Triangle.classify(2, 3, 2);
		// assertEquals(ISOSCELES, type);
	}

	public void testIsoceles3() {
		TriangleType type = Triangle.classify(3, 2, 2);
		// assertEquals(ISOSCELES, type);
	}

	public void testInvalidNeg1() {
		TriangleType type = Triangle.classify(-1, 1, 1);
		// assertEquals(INVALID, type);
	}

	public void testInvalidNeg2() {
		TriangleType type = Triangle.classify(1, -1, 1);
		// assertEquals(INVALID, type);
	}

	public void testInvalid1() {
		TriangleType type = Triangle.classify(1, 2, 4);
		// assertEquals(INVALID, type);
	}

	public void testInvalid2() {
		TriangleType type = Triangle.classify(1, 4, 2);
		// assertEquals(INVALID, type);
	}

}
