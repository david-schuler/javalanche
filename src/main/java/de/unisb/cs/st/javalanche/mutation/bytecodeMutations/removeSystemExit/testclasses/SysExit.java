package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.testclasses;

public class SysExit {

	private int lastFails;

	private int lastErrors;

	public int method1() {
		int i = Integer.parseInt("1");
		System.exit(0);
		return i;
	}
	
	public void method2() {
		int a = 4 + 4;
		if (a != Integer.MAX_VALUE) {
			System.exit(0);
		}
	}

	protected void systemExit(String messages) {
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
