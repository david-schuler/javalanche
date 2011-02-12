package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.AllTestsJunit3;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data.Junit3RegularPlusSuite;

public class Junit4MutationTestDriverTest2 {

	private MutationTestRunnable testRunnable;

	@Test
	public void test() throws Exception {

		String testname = Junit3RegularPlusSuite.class.getName();
		MutationProperties.TEST_SUITE = testname;
		Junit4MutationTestDriver j = new Junit4MutationTestDriver();

		List<String> allTests = j.getAllTests();
		assertEquals(2, allTests.size());
		System.out.println(allTests);
		testRunnable = j.getTestRunnable(testname + ".test1");
		Junit3RegularPlusSuite.check = 0;
		assertEquals(0, Junit3RegularPlusSuite.check);
		testRunnable.run();
		assertEquals(1, Junit3RegularPlusSuite.check);
		testRunnable = j.getTestRunnable(testname + ".test2");
		Junit3RegularPlusSuite.check = 0;
		testRunnable.run();
		assertEquals(2, Junit3RegularPlusSuite.check);

	}

}
