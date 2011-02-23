package de.unisb.cs.st.javalanche.mutation.javaagent;

import static de.unisb.cs.st.javalanche.mutation.properties.RunMode.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.coverage.CoverageTransformer;
import de.unisb.cs.st.javalanche.invariants.javaagent.InvariantTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationFileTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.MutationScanner;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanProjectTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.SysExitTransformer;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class MutationPreMainTest {

	private static JavalancheConfiguration configBack;
	private static JavalancheTestConfiguration config;
	private static File idFile;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		config = new JavalancheTestConfiguration();
		ConfigurationLocator.setJavalancheConfiguration(config);
		idFile = File.createTempFile("javalanche-temp-file", "txt", new File(
				"."));
		idFile.deleteOnExit();
	}

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}

	@Test
	public void testTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		config.setRunMode(CHECK_TESTS);
		mock.addTransformer((ClassFileTransformer) anyObject());
		// mock.addTransformer((ClassFileTransformer) anyObject());
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testMutaitonTestTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		config.setRunMode(MUTATION_TEST);
		config.setMutationIdFile(idFile);
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
		config.setRunMode(MUTATION_TEST_COVERAGE);
		config.setMutationIdFile(idFile);
		mock.addTransformer(isA(MutationFileTransformer.class));
		mock.addTransformer(isA(CoverageTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testScanTransformerIsAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		config.setRunMode(SCAN);
		mock.addTransformer(isA(MutationScanner.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testSysExitTransformerAdded() {
		config.setRunMode(CHECK_TESTS);
		checkIntegrateTestSuite();
		config.setRunMode(TEST_PERMUTED);
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
		config.setRunMode(CREATE_COVERAGE_MULT);
		mock.addTransformer(isA(SysExitTransformer.class));
		mock.addTransformer(isA(CoverageTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}

	@Test
	public void testScanProjectTransformerAdded() {
		Instrumentation mock = createMock(Instrumentation.class);
		config.setRunMode(SCAN_PROJECT);
		mock.addTransformer(isA(DistanceTransformer.class));
		mock.addTransformer(isA(ScanVariablesTransformer.class));
		mock.addTransformer(isA(ScanProjectTransformer.class));
		replay(mock);
		MutationPreMain.premain("", mock);
		verify(mock);
	}


}
