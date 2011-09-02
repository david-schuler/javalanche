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
package de.unisb.cs.st.javalanche.mutation.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverage;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class CoverageDataTest {

	private static Logger logger = Logger.getLogger(CoverageDataTest.class);

	private static final String TESTCLASS_NAME = "TESTCLASS";

	@Before
	@After
	public void setup() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("delete FROM Mutation WHERE classname=:name");
		query.setString("name", TESTCLASS_NAME);
		query.executeUpdate();
		tx.commit();
		session.close();
	}

	@Test
	public void mavenTest() {

	}

	private List<Mutation> getMutations(int limit) {
		ArrayList<Mutation> result = new ArrayList<Mutation>();
		for (int i = 0; i < limit; i++) {
			Mutation m = new Mutation(TESTCLASS_NAME, 23, i,
					MutationType.NEGATE_JUMP, false);
			result.add(m);
		}
		return result;
	}

	private List<String> getTestNames(int limit) {
		String baseName = "TESTNAME_";
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < limit; i++) {
			String name = String.format(baseName + "%2d", i);
			names.add(name);
		}
		return names;
	}

	public void deleteCoverage() {

	}

}
