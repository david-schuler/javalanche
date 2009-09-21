package de.unisb.cs.st.javalanche.mutation.hibernate;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;

public class CoverageDataTest {

	private static final int TESTS = 680;

	// @Test
	public void testConnection() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Exception e) {
			System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}
		try {
			Connection c = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/mt", "sa", "");
			assertNotNull(c);
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	



	// @Test
	public void testHugeCoverageDataSerialize() {
		Map<Long, Set<String>> map = getCoverageData();
		StopWatch stp = new StopWatch();
		stp.start();
		SerializeIo.serializeToFile(map, new File("/Users/schuler/test.ser"));
		stp.stop();
		System.out.println("TIME " + stp.getTime());
	}

	@Test
	public void testHugeCoverageDataSerialize2() {
		Map<Long, Set<String>> map = getCoverageData();
		StopWatch stp = new StopWatch();
		stp.start();
		MutationCoverageFile.saveCoverageData(map);
		MutationCoverageFile.reset();
		stp.stop();
		System.out.println("Saving coverage data took:" + stp.getTime());
		Set<String> coverageData = MutationCoverageFile.getCoverageDataId(10);
		Set<String> tests = getTests();
		assertEquals(tests.size(), coverageData.size());
		for (String test : coverageData) {
			assertTrue(tests.contains((test)));
		}
		
	}


	private Map<Long, Set<String>> getCoverageData() {
		Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();
		for (long i = 0; i < 100; i++) {
			Set<String> tests = getTests();
			map.put(i, tests);
		}
		return map;
	}

	private static Set<String> getTests() {
		Set<String> result = new HashSet<String>();
		for (int i = 0; i < TESTS; i++) {
			result.add("TEST_" + i);
		}
		return result;
	}
}
