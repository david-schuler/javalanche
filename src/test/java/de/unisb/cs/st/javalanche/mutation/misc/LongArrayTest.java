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
package de.unisb.cs.st.javalanche.mutation.misc;

import java.util.Random;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class LongArrayTest {


	@Test
	public void testLongArray(){
		MutationTestResult testResult = new MutationTestResult();
		int[] array = new int[100];
		Random r= new Random();
		for (int i = 0; i < array.length; i++) {
			array[i] = r.nextInt();
		}
		save(testResult, array);
		System.out.println(testResult);
		delete(testResult);

	}

	private void delete(MutationTestResult testResult) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.delete(testResult);
		tx.commit();
		session.close();
		}

	private void save(MutationTestResult testResult, int[] array) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		testResult.setViolatedInvariants(array);
		session.save(testResult);
		tx.commit();
		session.close();
	}
}
