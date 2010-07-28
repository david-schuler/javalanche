package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class AlwaysElse2TEMPLATE {

	public static int m(int x) {
		int res = -1;
		if (x != 10) {
			res = 5;
			if (x == 1) {
				res = 4;
			} else {
				res += 3;
			}
		} else {
			res += 4;
		}
		return res;
	}
}
