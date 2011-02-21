package de.unisb.cs.st.javalanche.mutation.properties;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class TestDefaultProperties {

	@Test
	public void testReleaseProperties() {
		PropertyConfiguration cfg = new PropertyConfiguration();
		assertFalse(DebugProperties.TRACE_BYTECODE);
		assertTrue(cfg.stopAfterFirstFail());
		assertThat(cfg.getDefaultTimeoutInSeconds(), is(10));
		assertFalse(cfg.storeTestMessages());
		assertFalse(cfg.storeTraces());
		assertThat(cfg.getSaveInterval(), is(50));
		assertTrue(cfg.enableMutationType(MutationType.REPLACE_CONSTANT));
		assertTrue(cfg.enableMutationType(MutationType.NEGATE_JUMP));
		assertTrue(cfg.enableMutationType(MutationType.ARITHMETIC_REPLACE));
		assertTrue(cfg.enableMutationType(MutationType.REMOVE_CALL));
		assertFalse(cfg.enableMutationType(MutationType.REPLACE_VARIABLE));
		assertThat(cfg.getTestPermutations(), is(10));
	}
}
