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
