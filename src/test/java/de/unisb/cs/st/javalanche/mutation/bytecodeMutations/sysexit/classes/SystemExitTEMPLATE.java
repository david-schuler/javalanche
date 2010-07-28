package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes;

public class SystemExitTEMPLATE {

	private int lastFails;

	private int lastErrors;

	public int m1() {
		int i = Integer.parseInt("1");
		System.exit(0);
		return i;
	}

	public int m2() {
		int a = 4 + 4;
		if (a != Integer.MAX_VALUE) {
			System.exit(0);
		}
		return a;
	}

	public void systemExit(String messages) {
		int num = lastFails; // messages.numMessages(IMessage.FAIL, true);
		if (0 < num) {
			System.exit(-num);
		}
		num = lastErrors; // messages.numMessages(IMessage.ERROR, false);
		if (0 < num) {
			System.exit(num);
		}
		System.exit(0);
	}

}
