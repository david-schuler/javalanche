package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValue.classes;

public class AbsoluteValueTEMPLATE {

	public boolean m1(int x) {
		int a = x;
		return a > 0;
	}

	public double m2(double d) {
		return 2 * d;
	}

	long l = -2l;

	public long m3(long x) {
		return l * x;
	}
}
