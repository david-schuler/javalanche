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
package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.classic.Session;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Class that writes all mutations for a project to a csv file.
 * 
 * @author David Schuler
 * 
 */
public class CsvWriter {

	private static Logger logger = Logger.getLogger(CsvWriter.class);

	public static void main(String[] args) throws IOException {
		// Set<Long> covered = MutationCoverageFile.getCoveredMutations();
		// List<Long> mutationIds = QueryManager.getMutationsWithoutResult(
		// covered, 0);

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Mutation> mutations = QueryManager.getMutationsForProject(
				MutationProperties.PROJECT_PREFIX, session);

		logger.info("Got " + mutations.size() + " mutation ids.");
		List<String> lines = new ArrayList<String>();
		lines.add(Mutation.getCsvHead() + ",DETECTED");
		int counter = 0;
		int flushs = 0;
		StopWatch stp = new StopWatch();
		for (Mutation mutation : mutations) {
			// Mutation mutation = QueryManager.getMutationByID(id, session);
			lines.add(mutation.getCsvString() + "," + mutation.isKilled());
			counter++;
			if (counter > 20) {
				counter = 0;
				// 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				stp.reset();
				stp.start();
				flushs++;
				session.flush();
				// session.clear();
				logger.info("Did flush. It took: "
						+ DurationFormatUtils.formatDurationHMS(stp.getTime()));
			}
		}
		session.close();
		logger.info("Starting to write file with " + lines.size() + " entries.");
		FileUtils.writeLines(new File("mutations.csv"), lines);
	}
}
