package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes;

public class RemoveCallsTEMPLATE {

	public int m1() {
		int result = 5;
		if (getBoolean()) {
			result = 23;
		}
		return result;
	}

	private boolean getBoolean() {
		return true;
	}

	public int m2(int x) {
		int result = 5;
		if (getObject(x) != null) {
			result = 23;
		}
		return result;
	}

	private Object getObject(int x) {
		if (x > 0) {
			return new Integer(3);
		}
		return null;
	}

	public int m3() {
		int result = 5;
		if (getInt() != 0) {
			result = 23;
		}
		return result;
	}

	private int getInt() {
		return 55;
	}

	public int m4() {
		int result = 23;
		int i = 3;
		if (getBoolean()) {
			i *= 2;
		}
		System.out.println("A");
		return result;
	}

}
