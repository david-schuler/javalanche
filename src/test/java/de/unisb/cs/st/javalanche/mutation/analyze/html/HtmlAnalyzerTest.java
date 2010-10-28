package de.unisb.cs.st.javalanche.mutation.analyze.html;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.TestContent;
import de.unisb.cs.st.javalanche.mutation.analyze.html.testcontent.TestContent1;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import static org.hamcrest.Matchers.*;

public class HtmlAnalyzerTest {

	@Before
	public void setUp() throws Exception {
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

	private void checkClass(String className, String checkValue) {
		HtmlAnalyzer a = new HtmlAnalyzer();
		Mutation m = new Mutation(className, "testM", 4, 1,
				MutationType.RIC_ZERO);
		List<Mutation> mutations = Arrays.asList(m);
		HtmlReport analyze = a.analyze(mutations);
		ClassReport classReport = analyze.getClassReport(className);
		String html = classReport.getHtml();
		assertThat(html, containsString(checkValue));
	}

}
