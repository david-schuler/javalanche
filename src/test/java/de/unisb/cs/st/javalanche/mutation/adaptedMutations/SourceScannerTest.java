package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SourceScannerTest {

	private void assertIf(int expectedStart, int expextedEnd,
			int expectedElseStart, IfStatementInfo info) {
		assertEquals("Expected different start for if", info.getIfStart(),
				expectedStart);
		assertEquals("Expected different end for if", info.getEnd(),
				expextedEnd);
		if (expectedElseStart >= 0) {
			assertEquals("Expected different start for else", info
					.getElseStart(), expectedElseStart);
			assertTrue(info.hasElse());
		}
	}

	@Test
	public void testIf1() throws Exception {
		String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.If1Class";
		String classNameSlash = className.replace('.', '/');
		File file = new File("src/test/java/" + classNameSlash + ".java");
		Map<String, ASTParseResult> result = SourceScanner
				.analyzeJavaFile(file);
		List<IfStatementInfo> ifStatementInfos = result.get(className)
				.getIfStatementInfos();
		assertEquals(ifStatementInfos.size(), 2);

		IfStatementInfo info1 = ifStatementInfos.get(0);
		IfStatementInfo info2 = ifStatementInfos.get(1);
		assertIf(6, 8, -1, info1);
		assertIf(12, 16, 14, info2);
	}

	@Test
	public void testIf2() throws Exception {
		String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.If2Class";
		String classNameSlash = className.replace('.', '/');
		File file = new File("src/test/java/" + classNameSlash + ".java");
		Map<String, ASTParseResult> result = SourceScanner
				.analyzeJavaFile(file);
		List<IfStatementInfo> ifStatementInfos = result.get(className)
				.getIfStatementInfos();
		assertEquals(2, ifStatementInfos.size());
		IfStatementInfo info1 = ifStatementInfos.get(0);
		IfStatementInfo info2 = ifStatementInfos.get(1);
		assertIf(7, 16, 13, info1);
		assertIf(20, 20, 20, info2);
	}

	@Test
	public void testIf3() throws Exception {
		String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.If3Class";
		String classNameSlash = className.replace('.', '/');
		File file = new File("src/test/java/" + classNameSlash + ".java");
		Map<String, ASTParseResult> result = SourceScanner
				.analyzeJavaFile(file);
		List<IfStatementInfo> ifStatementInfos = result.get(className)
				.getIfStatementInfos();
		assertEquals(3, ifStatementInfos.size());
		IfStatementInfo info1 = ifStatementInfos.get(0);
		IfStatementInfo info2 = ifStatementInfos.get(1);
		IfStatementInfo info3 = ifStatementInfos.get(2);
		assertIf(8, 20, 18, info1);
		assertIf(9, 17, 15, info2);
		assertIf(10, 14, 12, info3);
		assertTrue(info1.hasInnerIf());
		assertTrue(info2.hasInnerIf());
		assertFalse(info3.hasInnerIf());

	}

	@Test
	public void testIfElse() throws Exception {

		String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.IfElseClass";
		String classNameSlash = className.replace('.', '/');
		File file = new File("src/test/java/" + classNameSlash + ".java");
		Map<String, ASTParseResult> result = SourceScanner
				.analyzeJavaFile(file);
		List<IfStatementInfo> ifStatementInfos = result.get(className)
				.getIfStatementInfos();

		assertEquals(3, ifStatementInfos.size());
		IfStatementInfo info1 = ifStatementInfos.get(0);
		IfStatementInfo info2 = ifStatementInfos.get(1);
		IfStatementInfo info3 = ifStatementInfos.get(2);
		assertIf(6, 14, 8, info1);
		assertIf(8, 14, -1, info2);
		assertIf(10, 14, -1, info3);
	}

	@Test
	public void testDir() throws Exception {
		File file = new File(
				"src/test/java/de/unisb/cs/st/javalanche/mutation/adaptedMutations");
		Map<String, ASTParseResult> result = SourceScanner.parseDirectory(file);
		check(result);
	}

	@Test
	public void testWriteRead() throws Exception {
		File file = new File(
				"src/test/java/de/unisb/cs/st/javalanche/mutation/adaptedMutations");
		SourceScanner.parseAndSave(file);
		Map<String, ASTParseResult> result = SourceScanner.read();
		check(result);
	}

	private void check(Map<String, ASTParseResult> result) {
		String className1 = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.If1Class";
		String className2 = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.If2Class";
		String className3 = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.IfElseClass";
		ASTParseResult astParseResult1 = result.get(className1);
		assertNotNull(astParseResult1);
		ASTParseResult astParseResult2 = result.get(className2);
		assertNotNull(astParseResult2);
		ASTParseResult astParseResult3 = result.get(className3);
		assertNotNull(astParseResult3);
	}

	@Test
	public void testNested() throws Exception {

		String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.NestedIfClass";
		String classNameSlash = className.replace('.', '/');
		File file = new File("src/test/java/" + classNameSlash + ".java");
		Map<String, ASTParseResult> result = SourceScanner
				.analyzeJavaFile(file);

		assertEquals(4, result.size());
		assertTrue(result.containsKey(className));
		String key1 = className + "$1";
		assertTrue(result.containsKey(key1));
		String key2 = className + "$C1";
		assertTrue(result.containsKey(key2));
		String key3 = className + "$2";
		assertTrue(result.containsKey(key3));

		ASTParseResult astParseResult = result.get(className);
		ASTParseResult astParseResult1 = result.get(key1);
		ASTParseResult astParseResult2 = result.get(key2);
		ASTParseResult astParseResult3 = result.get(key3);

		assertEquals(0, astParseResult.getIfStatementInfos().size());
		assertEquals(1, astParseResult1.getIfStatementInfos().size());
		assertEquals(1, astParseResult2.getIfStatementInfos().size());
		assertEquals(1, astParseResult3.getIfStatementInfos().size());
	}
}
