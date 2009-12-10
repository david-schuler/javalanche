package de.unisb.cs.st.javalanche.coverage;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class CoveragePropertiesTest {

	@Test
	public void testRelease() {
		assertThat(CoverageProperties.TRACE_LINES, is(true));
		assertThat(CoverageProperties.TRACE_RETURNS, is(false));

	}

}
