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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.testClasses;


import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class AllTests extends TestCase {

    public static final boolean skipSupportModules = false;

    @Test
    public void testDoNothing(){
    	System.err.println("do nothing");
    	// to get rid of message
    	//junit.framework.AssertionFailedError: No tests found in de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite.testClasses.AllTests


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


// TODO REMOVE these classes
}
