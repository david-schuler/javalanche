/*
* Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;

import static org.junit.Assert.*;

import java.util.Map;

import junit.framework.TestSuite;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.testClasses.AllTests;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.TestSuiteUtil;

public class IntegrateSuiteTest {

	@Test
	public void runTests() {
		TestSuite suite = AllTests.suite();
		Map<String, junit.framework.Test> map = TestSuiteUtil.getAllTests(suite);
//		suite.run(new TestResult());
		TestSuite selectiveTestSuite = MutationTestSuite.toMutationTestSuite(suite);
		assertTrue(selectiveTestSuite!=null);
//		assertTrue(selectiveTestSuite instanceof SelectiveTestSuite);
		assertTrue(map.size() >= suite.testCount());
	}

}
