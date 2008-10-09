package de.st.cs.unisb.javalanche.javaagent.classFileTransfomer;

import static de.st.cs.unisb.javalanche.properties.MutationProperties.*;

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
