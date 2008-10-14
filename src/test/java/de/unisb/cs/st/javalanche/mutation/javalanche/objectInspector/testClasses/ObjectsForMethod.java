package de.unisb.cs.st.javalanche.mutation.objectInspector.testClasses;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ObjectsForMethod {

	private static Date staticDate = new Date();

	@SuppressWarnings("unused")
	public static int method1() {
		String string1 = "string1";

		int i = 4;
		List<String> listOfStrings = Arrays.asList(new String[] { "1", "2",
				"A", "B" });
		Date date = staticDate;
		Boolean bool = Boolean.FALSE;
		System.out.println("End of Method");
		return 1;
	}

	@SuppressWarnings("unused")
	public static int method2() {
		String string1 = "string1";
		int i = 4;
		List<String> listOfStrings = Arrays.asList(new String[] { "1", "2",
				"A", "B", "C" });
		Date date = staticDate;
		Boolean bool = Boolean.FALSE;
		System.out.println("End of Method");
		return 1;
	}

	// for testing purposes variablenames for different scopes.
	@SuppressWarnings("unused")
	public static int method3() {
		int a = (int) (Math.random() * 10);
		int b;
		if (a == 0) {
			int c = 0;
		} else {
			int d = 5;
			b = d * 2;
		}
		return a;
	}
}
