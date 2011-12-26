package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes;

public class WaitNotifyTEMPLATE {

	private static class HelperRunnable implements Runnable {

		private final Object o;

		private boolean finished;

		public HelperRunnable(Object o) {
			this.o = o;
		}

		@Override
		public void run() {
			synchronized (o) {
				try {
					o.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finished = true;
			}
		}

		public synchronized boolean isFinsished() {
			return finished;
		}
	}

	public static int m1() throws Exception {
		Object a = new Object();
		HelperRunnable hr1 = new HelperRunnable(a);
		HelperRunnable hr2 = new HelperRunnable(a);
		Thread t1 = new Thread(hr1);
		Thread t2 = new Thread(hr2);
		t1.start();
		t2.start();
		Thread.sleep(40);
		synchronized (a) {
			a.notifyAll();
		}
		Thread.sleep(40);
		int result = 0;
		result += hr1.isFinsished() ? 1 : 0;
		result += hr2.isFinsished() ? 1 : 0;
		synchronized (a) {
			a.notifyAll(); // Threads can finish executing when mutation is
							// applied
		}
		return result;
	}

	public static int m2() throws Exception {
		Object a = new Object();
		HelperRunnable hr1 = new HelperRunnable(a);
		HelperRunnable hr2 = new HelperRunnable(a);
		Thread t1 = new Thread(hr1);
		Thread t2 = new Thread(hr2);
		t1.start();
		t2.start();
		Thread.sleep(40);
		synchronized (a) {
			a.notify();
		}
		Thread.sleep(40);
		int result = 0;
		result += hr1.isFinsished() ? 1 : 0;
		result += hr2.isFinsished() ? 1 : 0;
		synchronized (a) {
			a.notifyAll(); // Threads can finish executing
		}
		return result;
	}
}
