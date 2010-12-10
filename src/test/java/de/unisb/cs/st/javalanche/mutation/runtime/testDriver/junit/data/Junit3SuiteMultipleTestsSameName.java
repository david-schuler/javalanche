package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import org.junit.Ignore;

import junit.framework.Test;
import junit.framework.TestSuite;

@Ignore
public class Junit3SuiteMultipleTestsSameName {


	public static Test suite() {
		TestSuite s = new TestSuite(
				Junit3SuiteMultipleTestsSameName.class.getName());
		// $JUnit-BEGIN$
		s.addTest(new Junit3TestWithConstructor(1));
		s.addTest(new Junit3TestWithConstructor(2));
		s.addTest(new Junit3TestWithConstructor(3));
		s.addTest(new Junit3TestWithConstructor(4));
		s.addTest(new Junit3TestWithConstructor(5));
		// $JUnit-END$
		return s;
	}

}
