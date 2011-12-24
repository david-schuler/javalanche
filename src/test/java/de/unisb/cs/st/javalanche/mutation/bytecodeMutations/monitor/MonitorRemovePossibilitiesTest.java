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

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor.classes.MonitorTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class MonitorRemovePossibilitiesTest {

	@Test
	public void testForOneClass() throws Exception {
		JavalancheConfiguration back = ConfigurationLocator
				.getJavalancheConfiguration();
		try {
			JavalancheTestConfiguration config = new JavalancheTestConfiguration();
			config.setMutationType(MONITOR_REMOVE, true);
			ConfigurationLocator.setJavalancheConfiguration(config);
			Class<?> clazz = MonitorTEMPLATE.class;
			ByteCodeTestUtils.deleteMutations(clazz.getCanonicalName());
			List<Mutation> possibilities = TestUtil
					.getMutationsForClazzOnClasspath(clazz);
			int possibilityCount = TestUtil.filterMutations(possibilities,
					MutationType.MONITOR_REMOVE).size();
			int expectedMutations = 1;
			assertEquals("Expected different number of mutations for class "
					+ clazz, expectedMutations, possibilityCount);
		} finally {
			ConfigurationLocator.setJavalancheConfiguration(back);
		}
	}
}
