package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.classic.Session;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class CsvWriter {

	private static Logger logger = Logger.getLogger(CsvWriter.class);

	public static void main(String[] args) throws IOException {
		Set<Long> covered = MutationCoverageFile.getCoveredMutations();
		List<Long> mutationIds = QueryManager.getMutationsWithoutResult(
				covered, 0);
		logger.info("Got " + mutationIds.size() + " mutation ids.");
		List<String> lines = new ArrayList<String>();
		lines.add(Mutation.getCsvHead());
		Session session = HibernateUtil.getSessionFactory().openSession();
		int counter = 0;
		int flushs = 0;
		StopWatch stp = new StopWatch();
		for (Long id : mutationIds) {
			Mutation mutation = QueryManager.getMutationByID(id, session);
			lines.add(mutation.getCsvString());
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
		logger
				.info("Starting to write file with " + lines.size()
						+ " entries.");
		FileUtils.writeLines(new File("mutations.csv"), lines);
	}
}
