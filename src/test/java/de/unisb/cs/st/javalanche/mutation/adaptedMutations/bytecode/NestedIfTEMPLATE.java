package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class NestedIfTEMPLATE {

	public static int m(int x) {
		int res = 0;
		if (x > 10) {
			res = 1;
			if (x % 2 == 0) {
				res = 2;
			} else {
				res = 3;
			}
		} else {
			res = 4;
		}
		return res;
	}

}
