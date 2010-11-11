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
package de.unisb.cs.st.javalanche.mutation.debug;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;
public class MissedMutationTest {


	@Test
	public void testLangUtil() {
		byte[] classBytes = TestProperties.LANG_UTIL_CLASS.getClassBytes();
		String className = TestProperties.LANG_UTIL_CLASS.getClassName();
		int size = TestUtil.getMutations(classBytes, className).size();
		assertThat(size, greaterThan(40));
	}
}
