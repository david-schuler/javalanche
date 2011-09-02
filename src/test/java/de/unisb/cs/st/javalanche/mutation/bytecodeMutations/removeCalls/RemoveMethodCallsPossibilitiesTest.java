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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes.SystemExitTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class RemoveMethodCallsPossibilitiesTest {

	@Test
	public void testForOneClass() throws Exception {
		Class<RemoveCallsTEMPLATE> clazz = RemoveCallsTEMPLATE.class;
		ByteCodeTestUtils.deleteMutations(clazz.getCanonicalName());
		List<Mutation> possibilities = TestUtil
				.getMutationsForClazzOnClasspath(clazz);
		int possibilityCount = TestUtil.filterMutations(possibilities,
				MutationType.REMOVE_CALL).size();
		int expectedMutations = 4;
		assertEquals("Expecting different number of mutations for class "
				+ clazz, expectedMutations, possibilityCount);
	}

	@Test
	public void testIgnoreSystemExit() throws Exception {
		Class<SystemExitTEMPLATE> clazz = SystemExitTEMPLATE.class;
		ByteCodeTestUtils.deleteMutations(clazz.getCanonicalName());
		List<Mutation> possibilities = TestUtil
				.getMutationsForClazzOnClasspath(clazz);
		int possibilityCount = TestUtil.filterMutations(possibilities,
				MutationType.REMOVE_CALL).size();
		int expectedMutations = 1;
		assertEquals("Expecting different number of mutations for class "
				+ clazz, expectedMutations, possibilityCount);

	}

}
