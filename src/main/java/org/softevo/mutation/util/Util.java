package org.softevo.mutation.util;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Class that contains several helper methods.
 * 
 * @author David Schuler
 * 
 */
public class Util {

	private static final Calendar instance = Calendar.getInstance();;

	/**
	 * Return a string wit the current stack trace (this method is excluded).
	 * 
	 * @return A string wit the current stack trace.
	 */
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
		String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		return time;

	}
}
