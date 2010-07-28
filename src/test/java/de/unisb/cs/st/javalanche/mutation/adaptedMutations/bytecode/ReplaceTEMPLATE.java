package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class ReplaceTEMPLATE {

	private int field = 10;

	public int m1(int x) {
		int v = 5;
		return x * v;
	}

	public int m2(int x) {
		int res = mx(x, 10);
		return res;
	}

	public int m3(int x) {
		return x * field;
	}

	private int mx(int i, int j) {
		return Math.min(i, j);
	}

}
