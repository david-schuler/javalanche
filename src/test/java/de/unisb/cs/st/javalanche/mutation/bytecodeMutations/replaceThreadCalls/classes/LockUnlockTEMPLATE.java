package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes;

import java.util.concurrent.locks.ReentrantLock;

public class LockUnlockTEMPLATE {

	int counter = 1;

	public boolean m1() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock(); 
		try {
			counter++;
		} finally {
			lock.unlock();
		}
		return lock.isLocked();
	}
}
