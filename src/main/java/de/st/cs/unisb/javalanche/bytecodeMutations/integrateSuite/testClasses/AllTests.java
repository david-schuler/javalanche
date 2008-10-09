package de.st.cs.unisb.javalanche.bytecodeMutations.integrateSuite.testClasses;


import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class AllTests extends TestCase {

    public static final boolean skipSupportModules = false;

    @Test
    public void testDoNothing(){
    	System.err.println("do nothing");
    	// to get rid of message
    	//junit.framework.AssertionFailedError: No tests found in de.st.cs.unisb.javalanche.bytecodeMutations.integrateSuite.testClasses.AllTests


    }
    public static TestSuite suite() {
        TestSuite suite =  new TestSuite(AllTests.class.getName());
        suite.addTest(new TestSuite2());
        suite.addTest(new AllTests("AllTests"));
        return suite;
    }

    public AllTests(String name) {
        super(name);
    }



}