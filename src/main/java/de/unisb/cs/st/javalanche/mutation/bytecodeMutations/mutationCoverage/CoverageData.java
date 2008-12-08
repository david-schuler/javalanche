package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.mutationCoverage;

import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationMarker;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Class to compute coverage data for mutations.
 *
 * When this class is used by a testdriver the method following methods have to
 * be called:
 * <ul>
 * <li>setTestName() at the beginning of every test. </li>
 * <li>{@link unsetTestName()} at the end of every test. </li>
 * <li>endCoverage() when the test suite has finished. </li>
 * </ul>
 *
 * @author David Schuler
 *
 */
public class CoverageData {

	private static final boolean SAVE_INTERVALLS = false;

	private static Logger logger = Logger.getLogger(CoverageData.class);

	// public ThreadLocal<String> testName = new ThreadLocal<String>();
	public String testName;

	private static class SingletonHolder {
		private final static CoverageData instance = new CoverageData();
	}

	/**
	 * Stores the coverage information. Maps a mutationId to an Set of tests.
	 */
	private Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();

	private Set<String> testsRun = new HashSet<String>();

	private int saveCount = 1;

	private CoverageData() {
	}

	public static CoverageData getInstance() {
		return SingletonHolder.instance;
	}

	public void saveAndEmpty() {
		logger.info("Saving coverage data for " + coverageData.size()
				+ " mutations");
		QueryManager.saveCoverageResults(coverageData);
		QueryManager.saveTestsWithNoCoverage(testsRun);
		// XmlIo.toXML(coverageData, "coverageData-" + saveCount + ".xml");
		saveCount++;
		coverageData = new HashMap<Long, Set<String>>();
	}

	static int call = 0;

	private static boolean shouldSave;

	public static void touch(long id) {

		call++;
		if (call % ((int) 1e6) == 0) {
			logger.info("Touch called " + call + "times.  Test "
					+ SingletonHolder.instance.getTestName()

					+ " touched mutation " + id);
			shouldSave = true;
		}
		// Only for debugging puprosses. Impacts performance
		// logger.info("Test " + SingletonHolder.instance.testName.get()
		// + " touched mutation " + id);
		logger.info("XYZ  `" + SingletonHolder.instance.getTestName() + "   " +  CoverageData.class.getClassLoader());
		Set<String> coveredTests = SingletonHolder.instance.coverageData
				.get(id);
		if (coveredTests == null) {
			coveredTests = new HashSet<String>();
			SingletonHolder.instance.coverageData.put(id, coveredTests);
		}
		coveredTests.add(SingletonHolder.instance.getTestName());
	}

	private String getTestName() {
		// testName.get()
		return testName;
	}

	public static void setTestName(String testName) {
		logger.info("Setting testname " + testName + "    " + CoverageData.class.getClassLoader());

		CoverageData instance = SingletonHolder.instance;
		instance.testsRun.add(testName);

		if (instance.getTestName()== null) {
			instance._setTestName(testName);
		} else {
			logger.info("Trying to overwrite testname");
			logger.info("Old testname: " + instance.getTestName());
			logger.info("New testname: " + testName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			logger.info("Stacktrace:\n" + stackTraceString);
		}
	}

	public static void unsetTestName(String testName) {
		logger.info("Unsetting testname " + testName);
		CoverageData instance = SingletonHolder.instance;
		String oldTestName = instance.getTestName();
		if (oldTestName == null) {
			logger.warn("Test name was  set to null expected " + testName);
		} else if (oldTestName.equals(testName)) {
			instance._setTestName(null);
		} else {
			logger.warn("Unset testname got different names");
			logger.warn("Tried to unset: " + testName);
			logger.warn("but got currently acctive test name: " + oldTestName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			logger.warn("Stacktrace:\n" + stackTraceString);
		}

	}

	private void _setTestName(String testName) {
		//testName.set(testName)
		this.testName = testName;
	}

	public static void main(String[] args) {
		touch(23454904540l);
	}

	public static void insertCoverageCalls(MethodVisitor mv, Mutation mutation) {
		Label endLabel = new Label();
		endLabel.info = new MutationMarker(false);
		Label mutationStartLabel = new Label();
		mutationStartLabel.info = new MutationMarker(true);
		mv.visitLabel(mutationStartLabel);
		Long id = mutation.getId();
		if (id == null) {
			Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDb != null) {
				id = mutationFromDb.getId();
			} else {
				QueryManager.saveMutation(mutation);
				id = mutation.getId();
			}
		}
		logger.debug("Inserting Coverage calls for:  " + id + " " + mutation);
		mv.visitLdcInsn(id);
		mv
				.visitMethodInsn(
						INVOKESTATIC,
						"de/unisb/cs/st/javalanche/mutation/bytecodeMutations/mutationCoverage/CoverageData",
						"touch", "(J)V");
		mv.visitLabel(endLabel);
	}

	public static void optionalSave() {
		if (SAVE_INTERVALLS && shouldSave) {
			CoverageData instance = SingletonHolder.instance;
			instance.saveAndEmpty();
			shouldSave = false;
		}
	}

	public static void endCoverage() {
		CoverageData instance = SingletonHolder.instance;
		instance.saveAndEmpty();
	}

	/**
	 * @return the testsRun
	 */
	public Set<String> getTestsRun() {
		return testsRun;
	}
}
