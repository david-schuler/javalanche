package triangle.tests;

import junit.framework.TestSuite;

import org.junit.Test;

import triangle.Triangle;
import triangle.TriangleType;
import static org.junit.Assert.*;
import static triangle.TriangleType.*;

public class Triangle1Test {

	@Test
	public void test1() {
		TriangleType type = Triangle.classify(1, 2, 3);
		assertEquals(SCALENE, type);
	}

	// @Test public void testInvalid1() {
	// TriangleType type = Triangle.classify(1, 2, 4);
	// assertEquals(INVALID, type);
	// }
	//
	// @Test public void testInvalid2() {
	// TriangleType type = Triangle.classify(1, 4, 2);
	// assertEquals(INVALID, type);
	// }

	@Test
	public void testInvalid3() {
		TriangleType type = Triangle.classify(4, 1, 2);
		assertEquals(INVALID, type);
		// throw new RuntimeException("AA");
	}

	//
	// @Test public void testInvalidNeg1() {
	// TriangleType type = Triangle.classify(-1, 1, 1);
	// assertEquals(INVALID, type);
	// }

	// @Test public void testInvalidNeg2() {
	// TriangleType type = Triangle.classify(1, -1, 1);
	// assertEquals(INVALID, type);
	// }
	//
	@Test
	public void testInvalidNeg3() {
		TriangleType type = Triangle.classify(1, 1, -1);
		assertEquals(INVALID, type);
	}

	@Test
	public void testEquiliteral() {
		TriangleType type = Triangle.classify(1, 1, 1);
		assertEquals(EQUILATERAL, type);
	}

	@Test
	public void testIsoceles1() {
		TriangleType type = Triangle.classify(2, 2, 3);
		assertEquals(ISOSCELES, type);
	}

	// @Test public void testIsoceles2() {
	// TriangleType type = Triangle.classify(2, 3, 2);
	// assertEquals(ISOSCELES, type);
	// }
	//
	// @Test public void testIsoceles3() {
	// TriangleType type = Triangle.classify(3, 2, 2);
	// assertEquals(ISOSCELES, type);
	// }
	//
	@Test
	public void testInvalid() {
		TriangleType type = Triangle.classify(3, 1, 1);
		assertEquals(INVALID, type);
	}

}
