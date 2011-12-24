package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;


public class MonitorUtil {

	private static class TestLock implements Runnable {

		private final Object o;

		public boolean gotLock = false;

		public TestLock(Object o) {
			this.o = o;
		}

		@Override
		public void run() {
			synchronized (o) {
				gotLock = true;
			}
		}

		public synchronized boolean gotLock() {
			return gotLock;
		}
	}

	public static boolean lockAvailable(final Object o) {
		TestLock tl = new TestLock(o);
		Thread t = new Thread(tl);
		t.start();
		try {
			t.join(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean result = tl.gotLock();
		return result;
	}

}
