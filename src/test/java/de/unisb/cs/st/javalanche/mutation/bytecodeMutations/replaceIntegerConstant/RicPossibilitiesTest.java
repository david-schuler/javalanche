/*
 * Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import static de.unisb.cs.st.javalanche.mutation.properties.TestProperties.*;
import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class RicPossibilitiesTest {

	@Test
	public void testForOneClass2() throws Exception {
		ByteCodeTestUtils.deleteMutations(ADVICE_CLASS.getClassName());
		List<Mutation> mutations = TestUtil.getMutations(
				ADVICE_CLASS.getClassBytes(), ADVICE_CLASS.getClassName());
		List<Mutation> filterMutations = TestUtil.filterMutations(mutations,
				MutationType.REPLACE_CONSTANT);
		int expectedMutations = 124;
		assertEquals("Expecting " + expectedMutations + " mutations",
				expectedMutations, filterMutations.size());
	}

}
