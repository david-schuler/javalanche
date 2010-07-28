package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class If3Class {

	int res;

	public void m1(int x) {
		if (x > 10) {
			if (x % 2 == 0) {
				if (x < 100) {
					res = 5;
				} else {
					res = 1;
				}
			} else {
				res = 3;
			}
		} else {
			res = -1;
		}
	}
}
