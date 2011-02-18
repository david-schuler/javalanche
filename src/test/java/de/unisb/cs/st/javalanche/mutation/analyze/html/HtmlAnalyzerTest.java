package de.unisb.cs.st.javalanche.mutation.analyze.html;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.ABCTestContent;
import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.TestContent;
import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.TestContent1;
import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.TestContentABC;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class HtmlAnalyzerTest {

	private static final String SEPERATOR = System
			.getProperty("file.separator");
	private JavalancheConfiguration back;

	@Before
	public void setUp() throws Exception {
		back = ConfigurationLocator.getJavalancheConfiguration();
		JavalancheTestConfiguration config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
		config.setProjectPrefix("de.unisb");
	}

	@After
	public void tearDown() {
		ConfigurationLocator.setJavalancheConfiguration(back);
	}

	@Test
	public void testClassContent() {
		String className = TestContent.class.getCanonicalName();
		checkClass(className, TestContent.value);
	}

	@Test
	public void testClassContent1() {
		String className = TestContent1.class.getCanonicalName();
		checkClass(className, TestContent1.value);
	}

	@Test
	public void testClassContentInner() {
		String className = TestContent1.class.getCanonicalName()
				+ "$InnerClass";
		checkClass(className, TestContent1.value);
	}

	@Test
	public void testClassContentABCpre() {
		String className = ABCTestContent.class.getCanonicalName();
		checkClass(className, ABCTestContent.value);
	}

	@Test
	public void testClassContentABCpost() {
		String className = TestContentABC.class.getCanonicalName();
		checkClass(className, TestContentABC.value);
	}

	private void checkClass(String className, String checkValue) {
		HtmlAnalyzer a = new HtmlAnalyzer();
		Mutation m = new Mutation(className, "testM", 4, 1,
				MutationType.REPLACE_CONSTANT);
		List<Mutation> mutations = Arrays.asList(m);
		HtmlReport analyze = a.analyze(mutations);
		ClassReport classReport = analyze.getClassReport(className);
		String html = classReport.getHtml();
		assertThat(html, containsString(checkValue));
	}

	@Test
	public void testGetClassName(){
		String canonicalName = TestContent.class.getCanonicalName();
		File f = new File("src/test/java/"
				+ canonicalName.replace(".", SEPERATOR));
		String className = HtmlAnalyzer.getContaingClassName(f);
		assertEquals(TestContent.class.getName(), className);
	}



}
