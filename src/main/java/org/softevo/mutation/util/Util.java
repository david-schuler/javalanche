package org.softevo.mutation.util;

import java.util.Arrays;

public class Util {

	public static String getStackTraceString() {
		Thread currentThread = Thread.currentThread();
		StackTraceElement[] sts = currentThread.getStackTrace();
		int arrayLength = sts.length - 3;
		StackTraceElement[] stackTrace = new StackTraceElement[arrayLength];
		System.arraycopy(sts, 3, stackTrace, 0, stackTrace.length);
		String stackTraceString = Arrays.toString(stackTrace);
		return stackTraceString;
	}

	public static void m1() {
		m2();
	}

	public static void m2() {
		m3();
	}

	public static void m3() {
		m4();
	}

	public static void m4() {
		System.out.println(getStackTraceString());
	}

	public static void main(String[] args) {
		m1();
	}
}
