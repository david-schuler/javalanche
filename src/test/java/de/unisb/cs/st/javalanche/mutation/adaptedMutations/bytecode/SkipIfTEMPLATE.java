package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class SkipIfTEMPLATE {

	public static int m(int x) {
		int res = -1;
		if (x == 10) {
			res = 13;
		}
		return res;
	}

}
