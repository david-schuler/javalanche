package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;


public class ElseIfReturnTEMPLATE {

	private static final boolean TRACE = false;

	public int m(int x) {
		if (TRACE) {
			System.out.println("TEST");
		}

		if (x == 1) {
			return 7;
		} else if (x == 1) {
			return 8;
		} else if (x == 3) {
			return 9;
		} else {
			return 10;
		}
	}
}
