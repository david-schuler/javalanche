package de.unisb.cs.st.javalanche.mutation.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class ThreadUtilTest {

	private static class MyThread extends Thread {
		private boolean run = true;

		public void run() {
			while (run) {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public synchronized void endThread() {
			run = false;
		}

	}

	@Test
	public void testGetThreads() {
		ArrayList<Thread> threads = ThreadUtil.getThreads();
		assertTrue(threads.size() > 3);
	}

	@Test
	public void testThreadCount() {

		ArrayList<Thread> threadsPre = ThreadUtil.getThreads();

		ArrayList<MyThread> generatedThreads = new ArrayList<MyThread>();

		for (int i = 0; i < 10; i++) {
			MyThread t = new MyThread();
			generatedThreads.add(t);
			t.start();
		}
		ArrayList<Thread> threadsPost = ThreadUtil.getThreads();
		assertEquals(threadsPost.size(), threadsPre.size()
				+ generatedThreads.size());
		for (MyThread thread : generatedThreads) {
			thread.endThread();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<Thread> threadsPost2 = ThreadUtil.getThreads();
		assertEquals(threadsPost2.size(), threadsPre.size());
	}

}
