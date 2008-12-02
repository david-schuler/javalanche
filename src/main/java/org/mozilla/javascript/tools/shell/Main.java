package org.mozilla.javascript.tools.shell;

import java.util.Arrays;

public class Main {

	static int exitCode;

	public static void main(String[] args) {
		exec(args);
	}

	public static int exec(String[] args) {
		throw new RuntimeException(
				"Wrong main test class called. Revise the order of the classpath. Given args:  "
						+ Arrays.toString(args));
	}
}
