package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor.classes;

import java.util.concurrent.Callable;

public class MonitorTEMPLATE {


	public static boolean m1(Callable<Integer> callable) throws Exception {
		synchronized (callable) {
				callable.call();
		}
		return true;
	}

	// public static boolean add(int[] array, int pos, int val) {
	// synchronized (array) {
	// array[pos] += val;
	// }
	// return true;
	// }

}
