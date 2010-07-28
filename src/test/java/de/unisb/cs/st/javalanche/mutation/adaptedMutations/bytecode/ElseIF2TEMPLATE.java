package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class ElseIF2TEMPLATE {

	public static int m(int x) {
		if (x == 10) {
			return 1;
		} else if (x > 10) {
			return 2;
		} else {
			return 3;
		}
	}

}
