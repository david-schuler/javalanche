package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

public class SkipIf2TEMPLATE {
	static boolean y = true;
	public static int m(int x) {
		int res = -1;
		if (y) {
			res = 13;
		}
		return res;
	}

}
