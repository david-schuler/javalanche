package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes;

import org.apache.commons.lang.time.StopWatch;

public class JoinSleepTEMPLATE {

	private static class MyRunnable implements Runnable {
		@Override
		public void run() {
		}
	}

	public long m1() throws InterruptedException {
		MyRunnable mr1 = new MyRunnable();
		Thread t1 = new Thread(mr1);
		t1.start();
		StopWatch stp = new StopWatch();
		stp.start();
		t1.sleep(110);
		stp.stop();
		long time = stp.getTime();
		return time;
	}

	public long m2() throws InterruptedException {
		MyRunnable mr1 = new MyRunnable();
		Thread t1 = new Thread(mr1);
		t1.start();
		StopWatch stp = new StopWatch();
		stp.start();
		t1.sleep(110, 45);
		stp.stop();
		long time = stp.getTime();
		return time;
	}

	public long m3() throws InterruptedException {
		MyRunnable mr1 = new MyRunnable();
		Thread t1 = new Thread(mr1);
		t1.start();
		StopWatch stp = new StopWatch();
		stp.start();
		t1.join(110);
		stp.stop();
		long time = stp.getTime();
		return time;
	}

	public long m4() throws InterruptedException {
		MyRunnable mr1 = new MyRunnable();
		Thread t1 = new Thread(mr1);
		t1.start();
		StopWatch stp = new StopWatch();
		stp.start();
		t1.join(110, 45);
		stp.stop();
		long time = stp.getTime();
		return time;
	}

}
