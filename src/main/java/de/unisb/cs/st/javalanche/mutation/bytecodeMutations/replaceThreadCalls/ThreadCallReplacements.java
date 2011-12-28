package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public class ThreadCallReplacements {

	private static final String sleepName = ".sleep";
	private static final String joinName = ".join";
	private static final String unlockName = ".unlock";
	private static final String lockName = ".lock";
	private static final String notifyAllName = ".notifyAll";
	private static final String notifyName = ".notify";
	private static final String objectName = "java/lang/Object";
	private static final String threadName = "java/lang/Thread";
	// private static final String reentrantLockName =
	// "java/lang/concurrent/ReentrantLock";
	private static final String reentrantLockName = "java/util/concurrent/locks/ReentrantLock";
	private static final String longIntVoidDesc = ".(JI)V";
	private static final String longVoidDesc = ".(J)V";
	private static final String noArgsVoidDesc = ".()V";

	static String[][] replaceArray = new String[][] {
			{ objectName + notifyName + noArgsVoidDesc,
					objectName + notifyAllName + noArgsVoidDesc },
			{ objectName + notifyAllName + noArgsVoidDesc,
					objectName + notifyName + noArgsVoidDesc },
			{ threadName + joinName + longIntVoidDesc,
					threadName + sleepName + longIntVoidDesc },
			{ threadName + joinName + longVoidDesc,
					threadName + sleepName + longVoidDesc },
			// { threadName + sleepName + longIntVoidDesc,
			// threadName + joinName + longIntVoidDesc },
			// { threadName + sleepName + longVoidDesc,
			// threadName + joinName + longVoidDesc },
			{ reentrantLockName + lockName + noArgsVoidDesc,
					reentrantLockName + unlockName + noArgsVoidDesc },
			{ reentrantLockName + unlockName + noArgsVoidDesc,
					reentrantLockName + lockName + noArgsVoidDesc }, };

	private static Map<String, String> replaceMap;

	private ThreadCallReplacements() {
	}

	public static Map<String, String> getReplaceMap() {
		if (replaceMap != null) {
			return replaceMap;
		}
		replaceMap = new HashMap<String, String>();
		for (String[] replace : replaceArray) {
			assert replace.length >= 2;
			replaceMap.put(replace[0], replace[1]);
		}
		return replaceMap;

	}
}
