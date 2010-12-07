package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;


import java.util.Random;

import junit.framework.TestCase;

public class DebugTestClass extends TestCase {
	public static final int GREGORIAN_MODE = 0;
	public static final int JULIAN_MODE = 1;

	private static final long MILLIS_PER_YEAR = (long) 365.2425 * 24 * 60 * 60
			* 1000;
	private static final long _1000_YEARS = 1000 * MILLIS_PER_YEAR;
	private static final long _500_YEARS = 500 * MILLIS_PER_YEAR;
	private static final long MAX_MILLIS = (10000 - 1970) * MILLIS_PER_YEAR;
	private static final long MIN_MILLIS = (-10000 - 1970) * MILLIS_PER_YEAR;

	// Show progess reports every 5 seconds.
	private static final long UPDATE_INTERVAL = 5000;

	/**
	 * Arguments: iterations [mode [seed]]
	 */
	public static void main(String[] args) throws Exception {
		int iterations = 1000000;
		int mode = GREGORIAN_MODE;
		long seed = 1345435247779935L;

		if (args.length > 0) {
			iterations = Integer.parseInt(args[0]);
			if (args.length > 1) {
				if (args[1].startsWith("g")) {
					mode = GREGORIAN_MODE;
				} else if (args[1].startsWith("j")) {
					mode = JULIAN_MODE;
				} else {
					throw new IllegalArgumentException("Unknown mode: "
							+ args[1]);
				}
				if (args.length > 2) {
					seed = Long.parseLong(args[2]);
				}
			}
		}

		new DebugTestClass(iterations, mode, seed).testObject();
	}

	// -----------------------------------------------------------------------
	private int iIterations;
	private int iMode;
	private long iSeed;;

	/**
	 * @param iterations
	 *            number of test iterations to perform
	 * @param mode
	 *            GREGORIAN_MODE or JULIAN_MODE,0=Gregorian, 1=Julian
	 * @param seed
	 *            seed for random number generator
	 */
	public DebugTestClass(int iterations, int mode, long seed) {

	}

	// -----------------------------------------------------------------------
	/**
	 * Main junit test
	 */
	public void testObject() {

	}

	// -----------------------------------------------------------------------
	private void testFields(long millis, int value, long millis2) {

	}

	private void testField(Object fieldA, Object fieldB, long millis,
			int value, long millis2) {

	}

	private int getWrappedValue(int value, int minValue, int maxValue) {
		return 0;
	}

	private void testValue(Object fieldA, Object fieldB, String method,
			long millis, long valueA, long valueB) {

	}

	private void testMillis(Object fieldA, Object fieldB, String method,
			long millis, long millisA, long millisB) {

	}

	private void testMillis(Object fieldA, Object fieldB, String method,
			long millis, long millisA, long millisB, int valueA, int valueB) {

	}

	private void testBoolean(Object fieldA, Object fieldB, String method,
			long millis, boolean boolA, boolean boolB) {

	}

	private void failValue(Object fieldA, Object fieldB, String method,
			long millis, long valueA, long valueB) {

	}

	private void failMillis(Object fieldA, Object fieldB, String method,
			long millis, long millisA, long millisB) {

	}

	private void failMillis(Object fieldA, Object fieldB, String method,
			long millis, long millisA, long millisB, int valueA, int valueB) {

	}

	private void failBoolean(Object fieldA, Object fieldB, String method,
			long millis, boolean boolA, boolean boolB) {

	}

	private String makeName(Object fieldA, Object fieldB) {
		return "";
	}

	private String makeDatetime(long millis) {
		return "";
	}

	private String makeDatetime(long millis, Object chrono) {
		return "";
	}

	private String makeDate(long millis) {
		return "";
	}

	private String makeDate(long millis, Object chrono) {
		return "";
	}

	// -----------------------------------------------------------------------
	private static long randomMillis(Random rnd) {
		return 1l;
	}

	private static void dump(Object chrono, long millis) {
	}

}
