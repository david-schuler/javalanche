package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class IfElseClass {

	public void m1(int x) {
		if (x > 100) {
			x--;
		} else if (x == 1) {
			x = 2;
		} else if (x == 2) {
			x = 22;
		} else {
			x = 345;
		}
	}
}
