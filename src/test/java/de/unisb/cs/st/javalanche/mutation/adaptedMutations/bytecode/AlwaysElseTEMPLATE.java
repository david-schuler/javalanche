package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class AlwaysElseTEMPLATE {

	public static int m(int x) {
		int res = -1;
		if (x != 10) {
			res = 5;

		} else {
			res += 4;

		}
		return res;
	}

}
