/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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
