package org.softevo.mutation.javaagent.classFileTransfomer;

import static org.softevo.mutation.properties.MutationProperties.*;

public class ClassFileTransformerUtil {

	private ClassFileTransformerUtil() {
	}

	public static boolean compareWithSuiteProperty(String classNameWithDots) {
		if (TEST_SUITE != null && classNameWithDots.contains(TEST_SUITE)) {
			return true;
		}
		return false;
	}
}
