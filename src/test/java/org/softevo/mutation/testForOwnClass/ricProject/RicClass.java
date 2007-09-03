package org.softevo.mutation.testForOwnClass.ricProject;

public class RicClass {

	public int method1(int i) {
		int myconst = 5;
		return i * myconst;
	}

	public long method2() {
		Long l = 100l;
		return 5 * l;
	}

	public boolean method3(int arg) {
		boolean b = true;
		return arg > -1000 && b;
	}
}
