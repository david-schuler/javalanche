package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;


public class Debug3TEMPLATE {


	public boolean m(int x) {
		try {
			if (x > 0) {
				return true;
			} else {
				return false;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}


}
