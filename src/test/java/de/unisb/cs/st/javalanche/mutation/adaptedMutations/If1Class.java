package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class If1Class {

	public void m1(int x) {
		if (x > 10) {
			x++;
		}
	}

	public void m2(int x) {
		if (x > 10) {
			x++;
		} else {
			x--;
		}
	}
}
