/*
 * Copyright (C) 2011 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.util;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtil {

	public static ArrayList<Thread> getThreads() {
		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}

		ArrayList<Thread> threads = new ArrayList<Thread>();
		visit(root, 0, threads);
		return threads;
	}

	// This method recursively visits all thread groups under `group'.
	public static void visit(ThreadGroup group, int level, List<Thread> result) {
		// Get threads in `group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[Math.max(numThreads, 2) * 2];
		numThreads = group.enumerate(threads, false);

		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			result.add(thread);
		}

		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			visit(groups[i], level + 1, result);
		}

	}
}
