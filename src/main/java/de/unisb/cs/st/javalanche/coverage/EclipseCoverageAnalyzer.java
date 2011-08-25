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
package de.unisb.cs.st.javalanche.coverage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class EclipseCoverageAnalyzer {
	private static Logger logger = Logger
			.getLogger(EclipseCoverageAnalyzer.class);

	public void analyze(String prefix) {
		CoverageAnalyzer mutationAnalyzer = new CoverageAnalyzer();
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE className LIKE '" + prefix
						+ "%'");
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		logger.info("Analyzing results for " + mutations.size() + "mutaitons");
		mutationAnalyzer.analyze(mutations, null);

		List<String> linesFromFile = Io
				.getLinesFromFile(new File("analyze.csv"));
		logger.info("Lines " + linesFromFile.size()); 
		Map<Long, Integer> impactMap = getImpactMap(linesFromFile);
		logger.info("Map size" + impactMap.size()); 
		for (Mutation m : mutations) {
			MutationTestResult result = m.getMutationResult();
			if (result != null) {
				int impact = 0;
				if (impactMap.containsKey(m.getId())) {
					impact = impactMap.get(m.getId());
				}
				logger.info("Seting impact " + impact + " for " + m.getId());
				// result.setDifferentViolatedInvariants(impact);
			}

		}
		tx.commit();
		session.close();

	}

	private Map<Long, Integer> getImpactMap(List<String> linesFromFile) {
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		for (String string : linesFromFile) {
			String[] split = string.split(";");
			String id_s = split[0];
			String impact_s = split[8];
			try {
				long id = Long.parseLong(id_s);
				int impact = Integer.parseInt(impact_s);
				map.put(id, impact);
			} catch (NumberFormatException e) {

			}
		}
		return map;
	}

	private void updateProperty() {
	}
}
