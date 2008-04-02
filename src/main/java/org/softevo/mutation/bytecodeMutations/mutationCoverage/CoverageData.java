package org.softevo.mutation.bytecodeMutations.mutationCoverage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

import static org.objectweb.asm.Opcodes.*;

public class CoverageData {

	private static Logger logger = Logger.getLogger(CoverageData.class);

	public ThreadLocal<String> testName = new ThreadLocal<String>();

	private static class SingletonHolder {
		private final static CoverageData instance = new CoverageData();
	}

	private Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();

	private CoverageData() {
	}

	public static CoverageData getInstance() {
		return SingletonHolder.instance;
	}

	static int call = 0;

	public static void touch(long id) {
		call++;
		if (call % 10000 == 0) {
			logger.info("touch called " + call + "times");
			logger.info("Test " + SingletonHolder.instance.testName.get()
					+ " touched mutation " + id);

		}
		// CoverageData instance = SingletonHolder.instance;
		Set<String> coveredTests = SingletonHolder.instance.coverageData
				.get(id);
		if (coveredTests == null) {
			coveredTests = new HashSet<String>();
			SingletonHolder.instance.coverageData.put(id, coveredTests);
		}
		coveredTests.add(SingletonHolder.instance.testName.get());
	}

	public static void setTestName(String testName) {
		logger.info("Setting testname " + testName);
		CoverageData instance = SingletonHolder.instance;
		if (instance.testName.get() == null) {
			instance.testName.set(testName);
		} else {
			logger.info("Trying to overwrite testname");
			logger.info("Old testname: " + instance.testName.get());
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
		String oldTestName = instance.testName.get();
		if (oldTestName == null) {
			logger.warn("Test name was  set to null expected " + testName);
		} else if (oldTestName.equals(testName)) {
			instance.testName.set(null);
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

	public static void main(String[] args) {
		touch(23454904540l);
	}

	public static void insertCoverageCalls(MethodVisitor mv, Mutation mutation) {

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
		mv.visitLdcInsn(mutation.getId());
		mv
				.visitMethodInsn(
						INVOKESTATIC,
						"org/softevo/mutation/bytecodeMutations/mutationCoverage/CoverageData",
						"touch", "(J)V");
	}

	public static void endCoverage() {
		QueryManager.saveCoverageResults(SingletonHolder.instance.coverageData);
		XmlIo.toXML(SingletonHolder.instance.coverageData, "coverageData.xml");
	}
}