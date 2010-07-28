package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class IfSwitchTEMPLATE {

	public int m(int x) {
		int res = 3;
		if (x > 0) {
			switch (x) {
			case 1:
				res += 2;
				break;

			default:
				res += 1;
				break;
			}
		} else {
			res = 2;
		}
		return res;
	}
}
