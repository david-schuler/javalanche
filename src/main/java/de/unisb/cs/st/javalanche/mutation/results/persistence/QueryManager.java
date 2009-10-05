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
package de.unisb.cs.st.javalanche.mutation.results.persistence;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.unisb.cs.st.ds.util.Formater;
import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverage;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

/**
 * Class that provides static method that execute queries.
 *
 * @author David Schuler
 *
 */
/**
 * @author David Schuler
 * 
 */
@SuppressWarnings("unchecked")
public class QueryManager {

	public static final String TEST_CASE_NO_INFO = "NO INFO";

	private static Logger logger = Logger.getLogger(QueryManager.class);

	private static SessionFactory sessionFactory = HibernateUtil
			.getSessionFactory();

	/**
	 * Prevent initialization.
	 */
	private QueryManager() {

	}

	/**
	 * Get Mutation that corresponds to given mutation from the database.
	 * 
	 * @throws RuntimeException
	 *             if no Mutation was found in the db.
	 * @param mutation
	 *            Mutation that is used to query.
	 * @return The Mutation from the database.
	 */
	public static Mutation getMutation(Mutation mutation) {
		Mutation m = getMutationOrNull(mutation);
		if (m == null) {
			throw new RuntimeException("Mutation not found in DB " + mutation);
		}
		return m;
	}

	/**
	 * Get Mutation that corresponds to given mutation from the database.
	 * 
	 * 
	 * @param mutation
	 *            Mutation that is used to query.
	 * @return The Mutation from the database or null if it is not contained.
	 */
	public static Mutation getMutationOrNull(Mutation mutation) {
		Session session = openSession();
		Mutation m = _getMutationOrNull(mutation, session);
		session.close();
		return m;
	}

	private static Mutation _getMutationOrNull(Mutation mutation,
			Session session) {
		Transaction tx = session.beginTransaction();
		Mutation m = null;
		if (mutation.getId() != null) {
			m = (Mutation) session.get(Mutation.class, mutation.getId());
		}
		if (m == null) {
			Query query = session
					.createQuery("from Mutation as m where m.className=:name and m.lineNumber=:number and m.mutationForLine=:mforl and m.mutationType=:type");
			query.setParameter("name", mutation.getClassName());
			query.setParameter("number", mutation.getLineNumber());
			query.setParameter("type", mutation.getMutationType());
			query.setParameter("mforl", mutation.getMutationForLine());
			m = (Mutation) query.uniqueResult();
		}
		tx.commit();
		return m;
	}

	/**
	 * Get Mutation that corresponds to given mutation from the database.
	 * 
	 * 
	 * @param mutation
	 *            Mutation that is used to query.
	 * @return The Mutation from the database or null if it is not contained.
	 */
	public static Mutation getMutationOrNull(Mutation mutation,
			Session session) {
		Mutation m = null;
		try {
			if (mutation.getId() != null) {
				m = (Mutation) session.get(Mutation.class, mutation.getId());
			}
			if (m == null) {
				Query query = session
						.createQuery("from Mutation as m where m.className=:name and m.lineNumber=:number and m.mutationForLine=:mforl and m.mutationType=:type");
				query.setParameter("name", mutation.getClassName());
				query.setParameter("number", mutation.getLineNumber());
				query.setParameter("type", mutation.getMutationType());
				query.setParameter("mforl", mutation.getMutationForLine());
				m = (Mutation) query.uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return m;
	}

	/**
	 * Set the result for the mutation and update the mutation in the database.
	 * 
	 * @param mutation
	 *            The Mutation to update.
	 * @param mutationTestResult
	 *            The result used to update.
	 */
	public static void updateMutation(Mutation mutation,
			MutationTestResult mutationTestResult) {
		mutation = getMutation(mutation);
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Mutation m2 = (Mutation) session.get(Mutation.class, mutation.getId());
		session.save(mutationTestResult);
		m2.setMutationResult(mutationTestResult);
		tx.commit();
		session.close();
	}

	public static void updateMutations(List<Mutation> results) {
		logger.info("Storing results for " + results.size() + " mutations");
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		int saved = 1;
		for (Mutation mutation : results) {
			Mutation mutationFromDB = (Mutation) session.get(Mutation.class,
					mutation.getId());
			if (mutationFromDB.getMutationResult() != null) {
				logger
						.warn("Mutation already has a test result - not storing the given result");
				logger.warn("Mutation:" + mutationFromDB);
				logger.warn("Result (that is not stored): " + mutation);
				session.setReadOnly(mutationFromDB, true);
				session.close();
				break;
			} else {
				session.save(mutation.getMutationResult());
				logger.debug("Setting result for mutation "
						+ mutationFromDB.getId());
				mutationFromDB.setMutationResult(mutation.getMutationResult());
				saved++;
			}
			if (saved % 20 == 0) { // 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				session.flush();
				session.clear();
			}
		}
		if (session.isOpen()) {
			tx.commit();
			session.close();
			logger.info("Succesfully stored results for " + results.size()
					+ " mutations");
		}
	}

	/**
	 * Fetches the given number of Mutations from the database, if there are
	 * that much.
	 * 
	 * @param maxResults
	 *            The number of Mutations to fetch.
	 * @return The List of fetched Mutations.
	 */
	@SuppressWarnings("unchecked")
	public static List<Mutation> getMutations(int maxResults) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation");
		query.setMaxResults(maxResults);
		List l = query.list();
		List<Mutation> mutations = new ArrayList<Mutation>();
		for (Object o : l) {
			mutations.add((Mutation) o);
		}
		tx.commit();
		session.close();
		return mutations;
	}

	/**
	 * Fetch all mutations for a given class from the database.
	 * 
	 * @param className
	 *            The name of the class in this form "java.lang.Object"
	 * @return A list of Mutations for this class.
	 */
	@SuppressWarnings("unchecked")
	public static List<Mutation> getAllMutationsForClass(String className) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where classname=:clName");
		query.setString("clName", className);
		List l = query.list();
		List<Mutation> mutations = (List<Mutation>) l;
		tx.commit();
		session.close();
		return mutations;
	}

	/**
	 * Saves given mutation in database if it is not already contained.
	 * 
	 * @param mutation
	 *            The mutation to save.
	 */
	public static synchronized void saveMutation(Mutation mutation) {
		Mutation mutationInDb = getMutationOrNull(mutation);
		if (mutationInDb != null) {
			logger.info("Mutation already contained - not saving to db: "
					+ mutation);
			assert mutationInDb.equalsWithoutId(mutation) : "Expected mutations to be equal: "
					+ mutation + "   " + mutationInDb;
		} else {
			logger.debug("Saving mutation: " + mutation);
			save(mutation);
		}
	}

	/**
	 * Save an object to the database, no checking is performed.
	 * 
	 * @param objectToSave
	 *            The object to save.
	 */
	public static void save(Object objectToSave) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		session.save(objectToSave);
		tx.commit();
		session.close();
	}

	/**
	 * Checks if there are mutations for given class in the database.
	 * 
	 * @param className
	 *            Name of the class.
	 * @return True, if there is on or more Mutation in the database.
	 */
	public static boolean hasMutationsforClass(String className) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("SELECT count(*) from Mutation where classname=:clname");
		query.setString("clname", className);
		List results = query.list();
		long l = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return l > 0;
	}

	public static long getResultFromCountQuery(String query) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(query);
		List results = q.list();
		long l = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return l;
	}

	public static long getResultFromSQLCountQuery(String query) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createSQLQuery(query);
		List results = q.list();
		long l = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return l;
	}

	public static long getResultFromCountQuery(List results) {
		long l = 0;
		if (results.size() > 0) {
			Object firstElement = results.get(0);
			if (firstElement instanceof Long) {
				l = (Long) firstElement;
			} else if (firstElement instanceof BigInteger) {
				l = ((BigInteger) firstElement).longValue();
			} else if (firstElement instanceof BigDecimal) {
				l = ((BigDecimal) firstElement).longValue();
			} else if (firstElement instanceof Integer) {
				l = ((Integer) firstElement).longValue();
			} else {
				throw new RuntimeException("Expected a long result. Got:  "
						+ firstElement.getClass());
			}
		} else {
			throw new RuntimeException("Got an empty list.");
		}
		return l;
	}

	/**
	 * Checks if the line of a given mutation is covered by a test case.
	 * 
	 * @param mutation
	 *            The mutation to check.
	 * @return True, if the line of the mutation is at least covered by one test
	 *         case.
	 */
	public static boolean isCoveredMutation(Mutation mutation) {
		Set<String> tests = MutationCoverageFile.getCoverageDataId(mutation
				.getId());
		return tests.size() > 0;
	}

	/**
	 * Returns the number of mutations that have a result associated.
	 * 
	 * @return The number of mutations that have a result associated.
	 */
	public static long getNumberOfMutationsWithResult() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String hibernateQuery = "SELECT count(*) FROM Mutation WHERE mutationResult IS NOT NULL";
		Query query = session.createQuery(hibernateQuery);
		List result = query.list();
		long l = getResultFromCountQuery(result);
		tx.commit();
		session.close();
		return l;
	}

	/**
	 * Query the database for mutations with the given ids.
	 * 
	 * @param ids
	 *            The ids that are used to query.
	 * @return Return a list of mutations with the given ids.
	 */
	public static List<Mutation> getMutationsFromDbByID(Long[] ids) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		// Query query = session
		// .createQuery("FROM Mutation m inner join fetch m.mutationResult inner
		// join fetch m.mutationResult.failures inner join fetch
		// m.mutationResult.errors inner join fetch m.mutationResult.passing
		// WHERE m.id IN (:ids)");
		Query query = session
				.createQuery("FROM Mutation m  WHERE m.id IN (:ids)");
		query.setParameterList("ids", ids);
		List<Mutation> results = query.list();
		int flushCount = 0;
		for (Mutation m : results) {
			m.loadAll();
			flushCount++;
			// if (flushCount % 10 == 0) {
			// session.flush();
			// session.clear();
			// }
		}
		tx.commit();
		session.close();
		return results;
	}

	/**
	 * Query the database for mutation with the given id.
	 * 
	 * @param id
	 *            The id that is used to query.
	 * @return The mutation with the given id.
	 */
	public static Mutation getMutationByID(Long id) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();

		Query query = session
				.createQuery("FROM Mutation m  WHERE m.id = (:ids)");
		query.setParameter("ids", id);
		List results = query.list();
		Mutation m = null;
		if (results.size() > 0) {
			m = (Mutation) results.get(0);
		}
		// Session.
		tx.commit();
		session.close();
		return m;
	}

	/**
	 * Generates a mutation of type not mutated in the database, if there is
	 * none in the db. The generated mutation or the one from the db is
	 * returned.
	 * 
	 * @param mutation
	 *            Mutation to generate a mutation of type not mutated for.
	 * @return The generated mutation or the mutation from the db.
	 */
	public static Mutation generateUnmutated(Mutation mutation) {
		Mutation unmutated;
		if (!hasUnmutated(mutation)) {
			unmutated = new Mutation(mutation.getClassName(), mutation
					.getLineNumber(), mutation.getMutationForLine(),
					MutationType.NO_MUTATION, mutation.isClassInit());
			saveMutation(unmutated);
		} else {
			unmutated = getUnmutated(mutation);
		}
		return unmutated;
	}

	private static Mutation getUnmutated(Mutation mutation) {
		return getUnmutated(mutation.getClassName(), mutation.getLineNumber());
	}

	public static boolean hasUnmutated(Mutation mutation) {
		return hasUnmutated(mutation.getClassName(), mutation.getLineNumber());
	}

	public static boolean hasUnmutated(String className, int lineNumber) {
		return getUnmutated(className, lineNumber) != null ? true : false;
	}

	public static Mutation getUnmutated(String className, int lineNumber) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where classname=:clname and linenumber=:lnumber and mutationtype=:mtype");
		query.setString("clname", className);
		query.setInteger("lnumber", lineNumber);
		query.setInteger("mtype", MutationType.NO_MUTATION.ordinal());
		Mutation mmutation = (Mutation) query.uniqueResult();
		tx.commit();
		session.close();
		return mmutation;
	}

	public static void saveMutations(Collection<Mutation> mutationsToSave) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		int counter = 0;
		for (Mutation mutation : mutationsToSave) {
			if (getMutationOrNull(mutation, session) != null) {
				logger.debug("Not saving mutation. Mutation already in db "
						+ mutation);
			} else {
				counter++;
				if (counter % 1000 == 0) {
					session.flush();
					session.clear();
				}
				try {
					logger.debug(counter + ": Trying to save mutation :"
							+ mutation);
					session.save(mutation);
				} catch (Exception e) {
					logger.warn("Exception thrown: " + e.getMessage());
					logger.info(Util.getStackTraceString());
					logger.info("Mutations to save: " + mutationsToSave);
					throw new RuntimeException(e);
				}
			}
		}
		tx.commit();
		session.close();

	}

	public static int getNumberOfMutationsForClass(String className) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createSQLQuery("SELECT count(*) FROM Mutation WHERE className = :clName AND mutationType!= 0;");
		query.setString("clName", className);
		Integer numberOfMutations = Integer.valueOf(query.uniqueResult()
				.toString());
		tx.commit();
		session.close();
		return numberOfMutations.intValue();
	}

	// /**
	// * Return the number of covered mutations with the set project prefix.
	// *
	// * @return the number of covered mutations with the set project prefix.
	// */
	// public static long getNumberOfCoveredMutations() {
	// String prefix = MutationProperties.PROJECT_PREFIX;
	// Session session = openSession();
	// Transaction tx = session.beginTransaction();
	// String queryString =
	// "SELECT count(DISTINCT mutationID) FROM MutationCoverage mc JOIN Mutation m ON mc.mutationID = m.id WHERE NOT m.classInit AND m.className LIKE '"
	// + prefix + "%'";
	// SQLQuery sqlQuery = session.createSQLQuery(queryString);
	// List results = sqlQuery.list();
	// long resultFromCountQuery = getResultFromCountQuery(results);
	// tx.commit();
	// session.close();
	// return resultFromCountQuery;
	// }

	public static List<Mutation> getMutationIdListFromDb(int numberOfMutations) {
		String prefix = MutationProperties.PROJECT_PREFIX;
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT m.* FROM Mutation m WHERE m.mutationType != 0 AND m.className LIKE '"
				+ prefix + "%' ";
		Query query = session.createSQLQuery(queryString).addEntity(
				Mutation.class);
		query.setMaxResults(numberOfMutations);
		List results = query.list();
		List<Mutation> idList = new ArrayList<Mutation>();
		for (Object mutation : results) {
			idList.add((Mutation) mutation);
		}
		tx.commit();
		session.close();
		return idList;
	}

	public static List<Long> getMutationsWithoutResult(Set<Long> ids, int limit) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT distinct(m.id) FROM Mutation m"
				+ " WHERE NOT m.classInit "
				+ " AND m.mutationResult_id IS NULL "
				+ " AND m.mutationType != 0" + " AND m.className LIKE '"
				+ MutationProperties.PROJECT_PREFIX + "%'"; // ORDER BY m.id ";
		logger.debug("Executing query: " + queryString);
		Query query = session.createSQLQuery(queryString);
		List results = query.list();
		List<Long> idList = new ArrayList<Long>();
		for (Object id : results) {
			Long l = Long.valueOf(id.toString());
			if (ids.contains(l)) {
				idList.add(l);
				if (limit != 0 && idList.size() > limit) {
					break;
				}
			}
		}
		tx.commit();
		session.close();
		return idList;
	}

	// /**
	// * Return a list of mutation ids that have coverage data associated but do
	// * not have a result yet.
	// *
	// * @return a list of mutation ids.
	// */
	// public static List<Long> getMutationsIdListFromDb(String prefix, int
	// limit) {
	//
	// Session session = openSession();
	// Transaction tx = session.beginTransaction();
	// String queryString = "SELECT distinct(m.id) FROM Mutation m"
	// + " JOIN MutationCoverage mc ON m.id = mc.mutationId"
	// +
	// " JOIN MutationCoverage_TestName mctn ON mc.id = mctn.MutationCoverage_id"
	// + " JOIN TestName tn ON mctn.testNames_id = tn.id"
	// + " WHERE tn.name !='NO INFO' " + " AND NOT m.classInit "
	// + " AND m.mutationResult_id IS NULL "
	// + " AND m.mutationType != 0" + " AND m.className LIKE '"
	// + prefix + "%' ORDER BY  m.id ";
	// if (!MutationProperties.COVERAGE_INFORMATION) {
	// queryString = "SELECT m.id FROM Mutation m "
	// + "WHERE  m.mutationresult_id IS NULL "
	// + "AND NOT m.classInit " + "AND m.mutationType != 0 "
	// + "AND m.className LIKE '" + prefix + "%' ";
	// }
	// logger.debug("Executing query: " + queryString);
	// Query query = session.createSQLQuery(queryString);
	// if (limit > 0) {
	// query.setMaxResults(limit);
	// }
	// List results = query.list();
	// List<Long> idList = new ArrayList<Long>();
	// for (Object id : results) {
	// idList.add(Long.valueOf(id.toString()));
	// }
	// tx.commit();
	// session.close();
	// return idList;
	// }

	/**
	 * Save the given coverage data to the database.
	 * 
	 * @param coverageData
	 *            a map that contains the collected coverage data
	 */
	private static void saveCoverageResults(Map<Long, Set<String>> coverageData) {
		logger.info("Start saving coverage data.");
		logger.info("Size of map: " + coverageData.size());
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from TestName");
		List<TestName> testNameQueryList = query.list();
		Map<String, TestName> testNameMap = new HashMap<String, TestName>();
		for (TestName tn : testNameQueryList) {
			testNameMap.put(tn.getName(), tn);
		}
		Map<Long, MutationCoverage> dbCoverageMap = new HashMap<Long, MutationCoverage>();
		Query mutationCoverageCountQuery = session
				.createQuery("select count(*) FROM MutationCoverage");
		List resultCount = mutationCoverageCountQuery.list();
		long l = getResultFromCountQuery(resultCount);
		int setSize = coverageData.keySet().size();
		if (l > 0 && setSize > 0) {
			List<Set<Long>> sets;
			if (setSize > 500) {
				logger.debug("Splitting key set because it is to large: "
						+ setSize);
				// http://opensource.atlassian.com/projects/hibernate/browse/HHH-1985
				sets = splitSet(coverageData.keySet(), 500);
			} else {
				sets = new ArrayList<Set<Long>>();
				sets.add(coverageData.keySet());
			}
			for (Set<Long> set : sets) {
				Query mutationCoverageQuery = session
						.createQuery("from MutationCoverage WHERE mutationID IN (:mids)");
				mutationCoverageQuery.setParameterList("mids", set);
				List<MutationCoverage> dbResults = mutationCoverageQuery.list();
				for (MutationCoverage mutationCoverage : dbResults) {
					dbCoverageMap.put(mutationCoverage.getMutationID(),
							mutationCoverage);
				}
			}
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		int saves = 0, flushs = 0;
		System.out
				.println("Writting coverage data to the database (This may take a while).");
		int count = 0, allcount = 0;
		for (Map.Entry<Long, Set<String>> entry : coverageData.entrySet()) {
			List<TestName> testNames = new ArrayList<TestName>();
			Long mutationID = entry.getKey();
			count++;
			saves++;
			for (String testCase : entry.getValue()) {
				if (testCase == null) {
					testCase = TEST_CASE_NO_INFO;
				}
				TestName testName = null;
				if (testNameMap != null && testNameMap.containsKey(testCase)) {
					testName = testNameMap.get(testCase);
				} else {
					testName = new TestName(testCase);
					session.save(testName);
					testNameMap.put(testCase, testName);
					saves++;
				}
				testNames.add(testName);
			}

			if (dbCoverageMap.containsKey(mutationID)) {
				dbCoverageMap.get(mutationID).addTestNames(testNames);
			} else {
				MutationCoverage mutationCoverage = new MutationCoverage(
						mutationID, testNames);
				session.save(mutationCoverage);
				saves++;
			}
			if (saves > MutationProperties.BATCH_SIZE) {
				// , same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				logger.debug(DurationFormatUtils.formatDurationHMS(stopWatch
						.getTime())
						+ "Temporary flush " + flushs + " -  Saves" + saves);
				saves = 0;
				long startFlush = System.currentTimeMillis();
				flushs++;
				session.flush();
				session.clear();
				long timeFlush = System.currentTimeMillis() - startFlush;
				logger.debug("Flush took: "
						+ Formater.formatMilliseconds(timeFlush));
				if (count > 500) {
					allcount += count;
					count = 0;
					String message = String.format("%3.2f percent saved",
							((allcount * 100.) / coverageData.size()));
					logger.info(message);
				}
				// Does not work for ant
				// System.out.print('.');
				// System.out.flush();
			}
		}
		tx.commit();
		session.close();
		logger.info("Coverage data saved to db");
	}

	private static List<Set<Long>> splitSet(Set<Long> keySet, int splitSize) {
		List<Set<Long>> list = new ArrayList<Set<Long>>();
		int count = 0;
		Set<Long> actualSet = null;
		for (Long l : keySet) {
			if (count % splitSize == 0) {
				actualSet = new HashSet<Long>();
				list.add(actualSet);
			}
			actualSet.add(l);
			count++;
		}
		return list;
	}

	// /**
	// * Returns the coverage data for given mutation id, or null if it has
	// none.
	// *
	// * @param id
	// * the id of the mutation.
	// * @return the coverage data or null
	// */
	// public static MutationCoverage getMutationCoverageData(long id) {
	// logger.debug("Getting coverage data for mutation " + id);
	// Session session = openSession();
	// Transaction tx = session.beginTransaction();
	// Query query = session
	// .createQuery("from MutationCoverage as m where m.mutationID = :mutation_id");
	// query.setLong("mutation_id", id);
	// MutationCoverage mc = (MutationCoverage) query.uniqueResult();
	// if (mc != null) {
	// Hibernate.initialize(mc.getTestsNames());
	// }
	// tx.commit();
	// session.close();
	// // if (mc == null) {
	// // logger.warn("found no coverage data for mutation with id " + id);
	// // }
	//
	// return mc;
	// }

	/**
	 * Delete Coverage data for given set of mutationids. rage data
	 * 
	 * @param ids
	 *            mutation ids that should be deleted.
	 */
	@SuppressWarnings("unchecked")
	public static void deleteCoverageResult(List<Long> ids) {
		if (ids.size() <= 0) {
			return;
		}
		logger.info("Deleting coverage information for " + ids.size()
				+ " mutations");
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		List<List<Long>> split = split(ids);
		for (List<Long> list : split) {

			Query query = session
					.createQuery("from MutationCoverage as m where m.mutationID IN (:mutation_ids)");
			query.setParameterList("mutation_ids", list);

			List<MutationCoverage> mcs = query.list();
			int deletes = 0;
			for (MutationCoverage mc : mcs) {
				session.delete(mc);
				deletes++;
				if (deletes % 20 == 0) {
					// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
					deletes = 0;
					session.flush();
					session.clear();
				}
			}
		}
		tx.commit();
		session.close();
	}

	private static List<List<Long>> split(List<Long> ids) {
		List<List<Long>> idSplit = new ArrayList<List<Long>>();
		List<Long> actualList = null;
		int count = 0;
		for (Long id : ids) {
			if (count % 1000 == 0) {
				actualList = new ArrayList<Long>();
				idSplit.add(actualList);
			}
			actualList.add(id);
			count++;
		}
		return idSplit;
	}

	public static void deleteCoverageResultByMutaiton(List<Mutation> mutations) {
		List<Long> ids = new ArrayList<Long>();
		for (Mutation mutation : mutations) {
			if (mutation.getId() != null) {
				ids.add(mutation.getId());
			} else {
				throw new RuntimeException("Mutation with id null" + mutation);
			}
		}
		deleteCoverageResult(ids);
	}

	// /**
	// *
	// * Add testnames to the database although they did not cover any mutation.
	// *
	// * @param testsRun
	// */
	// @SuppressWarnings("unchecked")
	// public static void saveTestsWithNoCoverage(Set<String> testsRun) {
	// Session session = openSession();
	// Transaction tx = session.beginTransaction();
	// Query query = session.createQuery("from TestName");
	// List<TestName> testNameQueryList = query.list();
	// List<String> testsToAdd = new ArrayList<String>(testsRun);
	// for (TestName tn : testNameQueryList) {
	// testsToAdd.remove(tn.getName());
	// }
	// for (String addTest : testsToAdd) {
	// session.save(new TestName(addTest));
	// }
	// tx.commit();
	// session.close();
	// }
	//
	// public static Set<String> getTestsCollectedData(Mutation mutation) {
	// MutationCoverage mutationCoverage = getMutationCoverageData(mutation
	// .getId());
	// if (mutationCoverage == null) {
	// return null;
	// }
	// List<TestName> testsNames = mutationCoverage.getTestsNames();
	// Set<String> tests = new HashSet<String>();
	// for (TestName testName : testsNames) {
	// String testNameString = testName.getName();
	// if (!testNameString.equals(TEST_CASE_NO_INFO)) {
	// tests.add(testNameString);
	// }
	// }
	// return tests;
	// }

	public static String mutationToShortString(Mutation m) {
		Session session = openSession();
		Mutation mutationOrNull = _getMutationOrNull(m, session);
		String result = null;
		if (mutationOrNull != null) {
			result = mutationOrNull.toShortString();
		}
		session.close();
		return result;
	}

	public static String mutationToString(Mutation m) {
		Session session = openSession();
		Mutation mutationOrNull = _getMutationOrNull(m, session);
		String result = null;
		if (mutationOrNull != null) {
			result = mutationOrNull.toString();
		}
		session.close();
		return result;
	}

	public static long getNumberOfMutationsWithPrefix(String projectPrefix) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("SELECT count(*) FROM Mutation WHERE className LIKE '"
						+ projectPrefix + "%' AND classInit=false");
		List results = query.list();
		long l = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return l;
	}

	public static List<Mutation> getMutationsForClass(String className) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		List<Mutation> list = getMutationsForClass(className, session);
		tx.commit();
		session.close();
		return list;
	}

	public static List<Mutation> getMutationsForClass(String className,
			Session session) {
		Query query = session
				.createQuery("from Mutation as m where m.className=:name");
		query.setParameter("name", className);
		List<Mutation> list = query.list();
		return list;
	}

	public static void main(String[] args) {
		List<Mutation> allMutationsForClass = getAllMutationsForClass("test.C1");
		System.out.println(allMutationsForClass);
	}

	public static TestName getTestName(String testCaseName) {
		TestName result = null;
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from TestName as tm where tm.name=:name");
		query.setParameter("name", testCaseName);
		List<TestName> list = query.list();
		if (list.size() > 0) {
			result = list.get(0);
		}
		tx.commit();
		session.close();
		return result;
	}

	private static Session openSession() {
		return sessionFactory.openSession();
	}

	public static long getNumberOfTestsForProject() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT count(DISTINCT name) FROM TestName WHERE project='"
				+ prefix + "'";
		SQLQuery sqlQuery = session.createSQLQuery(queryString);
		List results = sqlQuery.list();
		long resultFromCountQuery = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return resultFromCountQuery;
	}

	public static long getNumberOfTests() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT count(DISTINCT name) FROM TestName";
		SQLQuery sqlQuery = session.createSQLQuery(queryString);
		List results = sqlQuery.list();
		long resultFromCountQuery = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return resultFromCountQuery;
	}

	public static List<TestName> getTestsForProject() {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "FROM TestName WHERE project=:project";
		Query query = session.createQuery(queryString);
		query.setParameter("project", MutationProperties.PROJECT_PREFIX);
		List<TestName> results = query.list();
		tx.commit();
		session.close();
		return results;
	}

	public static void delete(Object tm) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		session.delete(tm);
		tx.commit();
		session.close();
	}

	/**
	 * @return the sessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public static void setSessionFactory(SessionFactory sessionFactory) {
		QueryManager.sessionFactory = sessionFactory;
	}

	/**
	 * Return the object with given id and class from the database.
	 * 
	 * @param id
	 *            the id of the object
	 * @param clazz
	 *            the class of the object to return
	 * @return the object with given id and class from the database.
	 */
	public static <T> T getObjectById(Long id, Class<T> clazz) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		T result = getObjectById(id, clazz, session);
		tx.commit();
		session.close();
		return result;
	}

	/**
	 * Return the object with given id and class from the database. If there is
	 * no object with the given id in the database then null is returned.
	 * 
	 * @param id
	 *            the id of the object
	 * @param clazz
	 *            the class of the object to return
	 * @param session
	 *            a session that is used for the query
	 * @return the object with given id and class from the database.
	 */
	public static <T> T getObjectById(Long id, Class<T> clazz, Session session) {
		T result = null;
		String name = clazz.getName();
		Query query = session.createQuery("FROM  " + name
				+ " n  WHERE n.id = (:id)");
		query.setParameter("id", id);
		List results = query.list();
		if (results.size() > 0) {
			T m = (T) results.get(0);
			result = m;
		}
		return result;
	}

	public static void deleteResult(Long id) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation m  WHERE m.id = (:ids)");
		query.setParameter("ids", id);
		List results = query.list();
		Mutation m = (Mutation) results.get(0);
		if (m != null) {
			MutationTestResult mutationResult = m.getMutationResult();
			m.setMutationResult(null);
			if (mutationResult != null) {
				session.delete(mutationResult);
			}
		}
		tx.commit();
		session.close();
	}

	public static void deleteMutations(String[] classes) {
		for (String clazz : classes) {
			deleteMutations(clazz);
		}
	}

	public static void deleteMutations(String clazz) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation m  WHERE m.className= (:classN)");
		query.setParameter("classN", clazz);
		List results = query.list();
		for (Object object : results) {
			session.delete(object);
		}
		tx.commit();
		session.close();
	}

	public static void deleteResults(String[] classes) {
		for (String clazz : classes) {
			deleteResults(clazz);
		}
	}

	public static void deleteResults(String clazz) {
		Session session = openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation m  WHERE m.className= (:classN)");
		query.setParameter("classN", clazz);
		List results = query.list();
		for (Object object : results) {
			if (object != null) {
				Mutation m = (Mutation) object;
				MutationTestResult mutationResult = m.getMutationResult();
				m.setMutationResult(null);
				if (mutationResult != null) {
					session.delete(mutationResult);
				}
			}
		}
		tx.commit();
		session.close();
	}

	/**
	 * Reads a list of mutation ids from a file and fetches the corresponding
	 * mutations from the database.
	 * 
	 * @param file
	 *            the file to read from
	 * @return a list of mutations read from the db.
	 */
	public static List<Mutation> getMutationsByFile(File file) {
		List<Long> idList = Io.getIDsFromFile(file);
		List<Mutation> returnList = null;
		if (idList.size() > 0) {
			returnList = getMutationsFromDbByID(idList
					.toArray(new Long[0]));
		} else {
			returnList = new ArrayList<Mutation>();
		}
		return returnList;
	
	}

}
