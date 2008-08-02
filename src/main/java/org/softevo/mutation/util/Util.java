package org.softevo.mutation.util;

import java.util.Arrays;
import java.util.Calendar;

public class Util {

	private static final Calendar instance = Calendar.getInstance();;

	public static String getStackTraceString() {
		Thread currentThread = Thread.currentThread();
		StackTraceElement[] sts = currentThread.getStackTrace();
		int arrayLength = sts.length - 3;
		StackTraceElement[] stackTrace = new StackTraceElement[arrayLength];
		System.arraycopy(sts, 3, stackTrace, 0, stackTrace.length);
		String stackTraceString = Arrays.toString(stackTrace);
		return stackTraceString;
	}

	public static String getTimeString() {
		instance.setTimeInMillis(System.currentTimeMillis());
		int hours = instance.get(Calendar.HOUR_OF_DAY);
		int minutes = instance.get(Calendar.MINUTE);
		int seconds = instance.get(Calendar.SECOND);
		String time = String
				.format("%02d:%02d:%02d", hours, minutes, seconds);
		return time;
	}

	private static void m1() {
		m2();
	}

	private static void m2() {
		m3();
	}

	private static void m3() {
		m4();
	}

	private static void m4() {
		System.out.println(getStackTraceString());
	}

	public static void main(String[] args) {
		m1();
	}
}
