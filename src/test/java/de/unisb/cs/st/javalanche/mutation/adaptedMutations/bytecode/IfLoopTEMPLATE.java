package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class IfLoopTEMPLATE {

	public int m(int x) {
		int res = 1;
		if (x > 0) {
			while (x > 0) {
				x--;
				res *= 2;
			}
			res++;
		} else {
			res = -1;
		}
		return res;
	}
}
