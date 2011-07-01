package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.classes;

public class ReplaceConstantTEMPLATE {

	public int m1(int i) {
		int myconst = 5;
		return i * myconst;
	}

	public long m2(int i) {
		long myconst = 5;
		return i * myconst;
	}

	public double m3(int i) {
		double myconst = 5;
		return i * myconst;
	}

	public float m4(int i) {
		float myconst = 5f;
		return i * myconst;
	}

	public boolean m5(int i) {
		int myconst = 5;
		return (i + myconst) > 10;
	}

	public double m6(double x) {
		double addVal = 2.2;
		return x + addVal;
	}

}
