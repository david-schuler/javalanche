package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.classes;

public class ReplaceIntegerTEMPLATE {

	public int m1(int i) {
		int myconst = 5;
		return i * myconst;
	}

	public long m2() {
		Long l = 100l;
		return 5 * l;
	}

	public boolean m3(int arg) {
		boolean b = true;
		int magicValue = 5;
		return arg == magicValue && b;
	}

}
