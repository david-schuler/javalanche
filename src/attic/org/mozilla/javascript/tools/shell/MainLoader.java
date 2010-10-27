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
