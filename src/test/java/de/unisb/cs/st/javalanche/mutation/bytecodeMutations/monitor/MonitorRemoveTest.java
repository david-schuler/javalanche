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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor.classes.MonitorTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class MonitorRemoveTest extends BaseBytecodeTest {

	private Class<?> clazz;

	private class MyCallable implements Callable<Integer> {

		int count = 0;

		@Override
		public Integer call() throws Exception {
			if (MonitorUtil.lockAvailable(this)) {
				count++;
			}
			return count;
		}

		public int getCount() {
			return count;
		}

	}

	public MonitorRemoveTest() throws Exception {
		super(MonitorTEMPLATE.class);
		config.setMutationType(MutationType.MONITOR_REMOVE, true);
		// verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testMonitorEnterRemove() throws Exception {
		Method m1 = clazz.getMethod("m1", Callable.class);
		MyCallable mc = new MyCallable();
		assertEquals(0, mc.getCount());
		checkUnmutated(new Object[] { mc }, true, m1, clazz);
		assertEquals(0, mc.getCount());
		checkMutation(9, MutationType.MONITOR_REMOVE, 0, new Object[] { mc },
				true, m1, clazz);
		assertEquals(1, mc.getCount());
	}

}
