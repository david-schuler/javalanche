package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject;

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
		int magicValue = 5;
		return arg == magicValue && b;
	}
}
