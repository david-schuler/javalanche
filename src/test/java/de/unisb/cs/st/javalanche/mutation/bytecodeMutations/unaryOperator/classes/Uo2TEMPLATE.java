package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperator.classes;

public class Uo2TEMPLATE {

	public static int CONST = 1;

	public int m1(int x) {
		if (x == CONST) {
			return 2;
		}
		return 0;
	}

	public static int CONST2 = 2;

	public int m2(int x) {
		if (x == CONST || x == CONST2) {
			return 3;
		}
		return 0;
	}

}
