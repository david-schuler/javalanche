package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class IfBreakTEMPLATE {

	public int m(int x) {
		int res = 0;
		if (x > 0) {
			switch (x) {
			case 1:
				res++;
				break;

			default:
				break;
			}
			res++;
		} else {
			res = 3;
		}
		return res;
	}
}
