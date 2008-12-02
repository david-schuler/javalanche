package org.mozilla.javascript.tools.shell;

import java.util.Arrays;

import de.unisb.cs.st.ds.util.SystemExitTool.SystemExitException;

public class MainLoader {

	private static int calls = 1;

	private static int exitCode;

	public static void main(String[] args) {
		System.out.println("MainLoader.main():  " + (calls++) + " call. Args:  "  + Arrays.toString(args));
		try {
			exitCode = Main.exec(args);
			// Main.main(args);
			// exitCode = Main.exitCode;
			System.out.println("MainLoader.main() 1");
		} catch (SystemExitException e) {
			e.printStackTrace();
			exitCode = 3;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
		System.out.println("MainLoader.main() Exit Code: " + exitCode);
	}

	/**
	 * @return the exitCode
	 */
	public static int getExitCode() {
		return exitCode;
	}

}