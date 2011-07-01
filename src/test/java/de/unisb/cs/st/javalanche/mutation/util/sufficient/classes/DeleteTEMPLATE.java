package de.unisb.cs.st.javalanche.mutation.util.sufficient.classes;

public class DeleteTEMPLATE {

	public int m1(int x) {
		int result = method(x);
		return result;
	}

	public int m2(int x) {
		return x >> x;
	}

	private int method(int x) {
		return x *= x;
	}
}
