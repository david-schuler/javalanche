package de.unisb.cs.st.javalanche.coverage;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.unisb.cs.st.javalanche.mutation.properties.RunMode.*;
import static org.easymock.EasyMock.*; //import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class CoverageTraceUtilTest {

	private Map<String, Map<String, Map<Integer, Integer>>> data1;
	private Map<String, Map<String, Map<Integer, Integer>>> data2;
	private Map<String, Map<Integer, Integer>> testMap1;
	private Map<String, Map<Integer, Integer>> testMap2;
	private Map<Integer, Integer> lineMap1;
	private Map<Integer, Integer> lineMap2;
	private String[] differences12;

	@Before
	public void setUp() throws Exception {

		data1 = new HashMap<String, Map<String, Map<Integer, Integer>>>();
		data2 = new HashMap<String, Map<String, Map<Integer, Integer>>>();

		testMap1 = new HashMap<String, Map<Integer, Integer>>();
		testMap2 = new HashMap<String, Map<Integer, Integer>>();
		lineMap1 = new HashMap<Integer, Integer>();
		lineMap2 = new HashMap<Integer, Integer>();

		lineMap1.put(1, 1);
		lineMap1.put(2, 1);

		lineMap2.put(1, 2);
		lineMap2.put(2, 2);
		lineMap2.put(3, 2);

		testMap1.put("method1", lineMap1);
		testMap1.put("method2", lineMap1);

		testMap2.put("method1", lineMap1);
		testMap2.put("method2", lineMap2);
		testMap2.put("method3", lineMap1);

		differences12 = new String[] { "method2", "method3" };
	}

	@Test
	public void testDifferentMethodsForSameData() {
		Collection<String> differentMethods = CoverageTraceUtil
				.getDifferentMethods(testMap1, testMap1);
		assertThat(differentMethods.size(), is(0));
	}

	@Test
	public void testDifferentMethods() {
		Collection<String> differentMethods = CoverageTraceUtil
				.getDifferentMethods(testMap1, testMap2);
		assertThat(differentMethods, hasItems(differences12));
		assertThat(differentMethods, not(hasItems("method1")));
	}

	@Test
	public void testDifferentMethodsForTests() {
		data1.put("test1", testMap1);
		data2.put("test1", testMap2);
		Collection<String> diffMethods = CoverageTraceUtil
				.getDifferentMethodsForTests(data1, data2);
		assertThat(diffMethods, hasItems(differences12));
	}

	@Test
	public void testNoDifferentMethodsForTests() {
		data1.put("test1", testMap1);
		data2.put("test1", testMap1);
		Collection<String> diffMethods = CoverageTraceUtil
				.getDifferentMethodsForTests(data1, data2);
		assertThat(diffMethods.size(), is(0));
	}

	@Test
	public void testDifferentTests() {
		data1.put("testA", testMap1);
		data2.put("testB", testMap1);
		Collection<String> diffMethods = CoverageTraceUtil
				.getDifferentMethodsForTests(data1, data2);
		assertThat(diffMethods.size(), is(0));
	}

	@Test
	public void testReadWrite() {
		String fileName = "test1.gz";
		CoverageTraceUtil.writeTrace(testMap1, "test1", fileName);
		Map<String, Map<Integer, Integer>> readMap = CoverageTraceUtil
				.loadTrace(new File(fileName));
		assertEquals(testMap1, readMap);
	}

	@Test
	public void testReadWrite2() {
		String fileName = "test1.gz";
		CoverageTraceUtil.writeTrace(testMap1, "test1", fileName);
		Map<String, Map<Integer, Integer>> readMap = CoverageTraceUtil
				.loadTrace(new File(fileName));
		Collection<String> diffMethods = CoverageTraceUtil.getDifferentMethods(
				testMap2, readMap);
		assertThat(diffMethods, hasItems(differences12));
	}

	@Test
	public void testReadDirectory() {
		File dir = new File("/rw-test-dir");
		dir.mkdir();
		dir.deleteOnExit();
		String fileName1 = new File(dir, "test1.gz").getAbsolutePath();
		String fileName2 = new File(dir, "test2.gz").getAbsolutePath();
		CoverageTraceUtil.writeTrace(testMap1, "test1", fileName1);
		CoverageTraceUtil.writeTrace(testMap2, "test2", fileName2);

		Map<String, Map<String, Map<Integer, Integer>>> traces = CoverageTraceUtil
				.loadTracesFromDirectory(dir);

		assertThat(traces.keySet(), hasItems("test1", "test2"));
		assertEquals(testMap1, traces.get("test1"));
		assertEquals(testMap2, traces.get("test2"));
	}

}
