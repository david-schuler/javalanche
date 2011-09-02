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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class ArtihmeticPossibilitiesTest {

	@Test
	public void testPossibilities() throws IOException {
		byte[] classBytes = TestProperties.ADVICE_CLASS.getClassBytes();
		String className = TestProperties.ADVICE_CLASS.getClassName();
		QueryManager.deleteMutations(className);
		List<Mutation> mutations = TestUtil.getMutations(classBytes, className);
		int res = TestUtil.filterMutations(mutations,
				MutationType.ARITHMETIC_REPLACE).size();
		assertThat(res, greaterThan(10));
	}
}
