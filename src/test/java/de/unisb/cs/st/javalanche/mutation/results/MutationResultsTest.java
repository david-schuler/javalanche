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
package de.unisb.cs.st.javalanche.mutation.results;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MutationResultsTest {

	private MutationTestResult mtr;


	@Before
	public void setUp() {
		mtr = new MutationTestResult();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testDate() {
		Date date = mtr.getDate();
		Date expectDate = new Date();
		assertNotNull(date);
		assertEquals(getHour(expectDate), getHour(date));
	}

	private int getHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	@Test
	public void testAddFailure(){
		TestMessage tm = new TestMessage("Test", "message", 10l);
		assertEquals(0,mtr.getFailures().size());
		mtr.addFailure(tm);
		assertEquals(1,mtr.getFailures().size());
		mtr.addFailure(tm);
		assertEquals(2,mtr.getFailures().size());
	}
}
