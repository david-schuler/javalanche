package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject;

public class IntegerConstants {

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
		int threshold = 5;
		return arg == threshold && b;
	}
}
