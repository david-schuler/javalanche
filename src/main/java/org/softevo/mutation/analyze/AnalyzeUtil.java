package org.softevo.mutation.analyze;

public class AnalyzeUtil {

	static String formatPercent(double d) {
		return String.format("%2.2f%%", d * 100);
	}

	public static String formatPercent(int fraction, int total) {
		return formatPercent((double) fraction / total);
	}

}
