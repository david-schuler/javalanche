package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Invariant;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class InvariantDbReader {

	private static Logger logger = Logger.getLogger(InvariantDbReader.class);

	private static final Pattern p = Pattern.compile("(.*)\\((.*)\\)");

	private static final String DEFAULT_FILENAME = "trace.out";

	private static final String ADABU2_RESULTFILENAME = "adabu2.resultfilename";

	public static void main(String[] args) {
		invariantsToDB();
		displayInvariants();
	}

	private static void invariantsToDB() {
		Collection<InvariantChecker> allInvariants = InvariantProperties
				.getClassInvariants().getInvariants();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		int saved = 0;
		for (InvariantChecker invariantChecker : allInvariants) {
			Invariant in = new Invariant(invariantChecker);
			session.save(in);
			saved++;
			if (saved % 20 == 0) { // 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				session.flush();
				session.clear();
			}
		}
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	private static void displayInvariants() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Invariant");
		query.setMaxResults(100);
		List list = query.list();
		for (Object object : list) {
			System.out.println(object);

		}
		tx.commit();
		session.close();
	}

//	private static void invariantsToDBComplex() {
//		String baseFileName = System.getProperty(ADABU2_RESULTFILENAME,
//				DEFAULT_FILENAME);
//		File f = new File(baseFileName + "-file-mapping.txt");
//		List<String> linesFromFile = Io.getLinesFromFile(f);
//		Map<String, String> fileMapping = new HashMap<String, String>();
//		for (String line : linesFromFile) {
//			System.out.println("Line:  " + line);
//			String[] split = line.split(",");
//
//			String fileName = split[0];
//			// System.out.println("Trying to match " + split[1]);
//			Matcher m = p.matcher(split[1]);
//			boolean b = m.matches();
//			System.out.println(b ? " Matched " : " Did not match " + fileName);
//			System.out
//					.println(fileName + "   " + m.group(2) + "." + m.group(1));
//			String testName = m.group(2) + "." + m.group(1);
//			fileMapping.put(testName, fileName);
//
//		}
//
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
//		Collection<InvariantChecker> allInvariants = InvariantProperties
//				.getClassInvariants().getInvariants();
//		for (InvariantChecker invariantChecker : allInvariants) {
//			session
//					.save(new de.unisb.cs.st.javalanche.mutation.results.Invariant(
//							invariantChecker));
//		}
//		Set<Entry<String, String>> entrySet = fileMapping.entrySet();
//		for (Entry<String, String> entry : entrySet) {
//			String testName = entry.getKey();
//			Query query = session.createQuery("from TestName where name=:name");
//			query.setString("name", testName);
//			List list = query.list();
//			if (list.size() > 0) {
//				System.out.println("Found entry");
//				File invariantFile = getInvariantFileName(entry.getValue());
//				Iterator<PptTopLevel> topLevelPpts = null;
//				try {
//					PptMap ppts = FileIO.read_serialized_pptmap(invariantFile,
//							false);
//					topLevelPpts = ppts.ppt_all_iterator();
//				} catch (Throwable e) {
//					e.printStackTrace();
//					Runtime r = Runtime.getRuntime();
//					r.gc();
//					logger.warn("Exception when reading file" + invariantFile,
//							e);
//				}
//				logger.info("File read");
//				InvariantFilters filters = InvariantFilters.defaultFilters();
//				InvariantCollector collector = new InvariantCollector();
//				while (topLevelPpts != null && topLevelPpts.hasNext()) {
//					PptTopLevel ppt = topLevelPpts.next();
//					if (ppt.name().endsWith("ENTER")
//							|| ppt.name().endsWith("EXIT1")) {
//						Iterator<Invariant> invariantIterator = ppt
//								.invariants_iterator();
//						while (invariantIterator.hasNext()) {
//							Invariant invariant = invariantIterator.next();
//							try {
//								if (filters.shouldKeep(invariant) != null) {
//									collector.processInvariant(invariant);
//									String invariantClass = invariant
//											.getClass().getName();
//
//								}
//							} catch (Throwable t) {
//								logger.warn("Throwable caught.", t);
//							}
//						}
//					}
//				}
//				ClassInvariants invariants = collector.getInvariants();
//
//				for (InvariantChecker checker : invariants.getInvariants()) {
//					if (allInvariants.contains(checker)) {
//						System.out.println("Found invariant of this test");
//					}
//
//				}
//			} else {
//
//				System.out.println("Did not find entry for " + testName);
//			}
//
//		}
//
//		tx.commit();
//		session.close();
//	}

	private static File getInvariantFileName(String value) {
		logger.info("Getting invariant file for " + value);
		File f = new File(value);
		String name = f.getName();
		String replace = name.replace("trace.out", "daikon-");
		replace += ".inv.gz";

		logger.info("Invariant file: " + replace);
		return new File(f.getParentFile(), replace);
	}

	@SuppressWarnings("unchecked")
	public void mapInvariants() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = MutationProperties.PROJECT_PREFIX;
		Query query = session.createQuery("from Mutation WHERE name LIKE '"
				+ projectPrefix + "%'");
		List<Mutation> list = query.list();
		for (Mutation mutation : list) {
			MutationTestResult mutationResult = mutation.getMutationResult();
			if (mutationResult != null) {
				int[] violatedInvariants = mutationResult
						.getViolatedInvariants();
				Query invariantQuery = session
						.createQuery("from Invariant WHERE checkerId in (:ids)");
				invariantQuery.setParameterList("ids", Arrays
						.asList(violatedInvariants));
				List<de.unisb.cs.st.javalanche.mutation.results.Invariant> invariantList = invariantQuery
						.list();
				for (de.unisb.cs.st.javalanche.mutation.results.Invariant invariant : invariantList) {
					logger.info("adding invariant" + invariant);
					mutationResult.addInvariant(invariant);
				}
			}
			session.update(mutationResult);
		}
		tx.commit();
		session.close();

	}

}
