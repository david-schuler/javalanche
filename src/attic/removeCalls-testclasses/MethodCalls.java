package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses;

public class MethodCalls {

	public int supressFail1() {
		int result = 5;
		if (getBoolean()) {
			result = 23;
		}
		return result;
	}

	private boolean getBoolean() {
		return true;
	}

	public int supressFail2() {
		int result = 5;
		if (getObject() != null) {
			result = 23;
		}
		return result;
	}

	private Object getObject() {
		return new Integer(3);
	}

	public int supressFail3() {
		int result = 5;
		if (getInt() != 0) {
			result = 23;
		}
		return result;
	}

	private int getInt() {
		return 55;
	}

	public int ignoreMethodForResult() {
		int result = 23;
		int i = 3;
		if (getBoolean()) {
			i *= 2;
		}
		return result;
	}

}
