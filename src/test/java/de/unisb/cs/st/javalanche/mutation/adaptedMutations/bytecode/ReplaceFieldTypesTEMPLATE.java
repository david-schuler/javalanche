package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class ReplaceFieldTypesTEMPLATE {

	int x = 1;
	short s = 2;
	char c = (char) 3;
	byte by = 4;
	float f = 5.f;
	double d = 6.;
	long l = 7l;
	boolean b = true;
	String o = "a";

	public int m1(int i) {
		return i * x;
	}

	public int m2(int i) {
		return i * s;
	}

	public int m3(int i) {
		return i * c;
	}

	public int m4(int i) {
		return i * by;
	}

	public int m5(int i) {
		return (int) (i * f);
	}

	public int m6(int i) {
		return (int) (i * d);
	}

	public int m7(int i) {
		return (int) (i * l);
	}

	public int mb(int i) {
		return b ? i : 0;
	}

	public int mo(int i) {
		return o != null ? i : 0;
	}
}
