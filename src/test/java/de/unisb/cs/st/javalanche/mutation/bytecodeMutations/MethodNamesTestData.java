package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.File;

public class MethodNamesTestData {

	public MethodNamesTestData() {
		dummy(); // Line 8
	}

	public MethodNamesTestData(int x) {
		dummy();// Line 12
	}

	public void dummy() {
		int i = 111 + 10; // Line 16
	}

	public void dummy2(int i, String s, File f) {
		dummy(); // Line 20
	}

}
