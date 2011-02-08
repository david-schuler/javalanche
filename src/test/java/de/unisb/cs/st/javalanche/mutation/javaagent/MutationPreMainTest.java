package de.unisb.cs.st.javalanche.mutation.javaagent;

import static de.unisb.cs.st.javalanche.mutation.properties.RunMode.*;
import static org.easymock.EasyMock.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.junit.Test;

import de.unisb.cs.st.javalanche.coverage.CoverageTransformer;
import de.unisb.cs.st.javalanche.invariants.javaagent.InvariantTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanProjectTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.SysExitTransformer;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class MutationPreMainTest {

	@Test
	public void testTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = CHECK_TESTS;
		mock.addTransformer((ClassFileTransformer) anyObject());
		// mock.addTransformer((ClassFileTransformer) anyObject());
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testMutaitonTestTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = MUTATION_TEST;
		mock.addTransformer(isA(MutationFileTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}


	private void checkInvariantTransformer() {
		Instrumentation mock = createMock(Instrumentation.class);
		mock.addTransformer(isA(InvariantTransformer.class));
		mock.addTransformer(isA(MutationFileTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testCoverageTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = MUTATION_TEST_COVERAGE;
		mock.addTransformer(isA(MutationFileTransformer.class));
		mock.addTransformer(isA(CoverageTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testScanTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = SCAN;
		System.setProperty(MutationProperties.TEST_SUITE_KEY, "test");
		mock.addTransformer(isA(MutationScanner.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testSysExitTransformerAdded() {
		MutationProperties.RUN_MODE = CHECK_TESTS;
		checkIntegrateTestSuite();
		MutationProperties.RUN_MODE = TEST_PERMUTED;
		checkIntegrateTestSuite();

	}

	private void checkIntegrateTestSuite() {
		Instrumentation mock = createMock(Instrumentation.class);
		mock.addTransformer(isA(SysExitTransformer.class));
		// mock.addTransformer(isA(IntegrateTestSuiteTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testCoverageTransformerAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = CREATE_COVERAGE_MULT;
		mock.addTransformer(isA(SysExitTransformer.class));
		mock.addTransformer(isA(CoverageTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testScanProjectTransformerAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		MutationProperties.RUN_MODE = SCAN_PROJECT;
		mock.addTransformer(isA(DistanceTransformer.class));
		mock.addTransformer(isA(ScanVariablesTransformer.class));
		mock.addTransformer(isA(ScanProjectTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}


}
