package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheDefaultConfiguration;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class JavalancheTestConfiguration implements JavalancheConfiguration {

	JavalancheDefaultConfiguration d = new JavalancheDefaultConfiguration();
	private String excludedTests = d.getExcludedTests();
	private int timeOut = d.getTimeoutInSeconds();
	private File excludedFile = d.getExcludeFile();
	private String ignorePattern = d.getIgnorePattern();
	private File mutationIdFile = d.getMutationIdFile();
	private File outputDir = d.getOutputDir();
	private String projectPrefix = d.getProjectPrefix();
	private int saveIntervall = d.getSaveInterval();
	private RunMode runMode = d.getRunMode();
	private String sourceDir = d.getProjectSourceDir();
	private File excludeFile = d.getExcludeFile();
	private String testNames = d.getTestNames();
	private int testPermutations = d.getTestPermutations();
	private boolean stopAfterFirstFail = d.stopAfterFirstFail();
	private boolean storeTestMessages = d.storeTestMessages();
	private boolean storeTraces = d.storeTraces();
	private boolean useThreadStop = d.useThreadStop();

	private Map<MutationType, Boolean> typeEnabled = initializeMap();
	private boolean runAllTestsForMutation = d.runAllTestsForMutation();
	private boolean useJunit3Runner = d.useJunit3Runner();

	private Map<MutationType, Boolean> initializeMap() {
		Map<MutationType, Boolean> map = new HashMap<MutationType, Boolean>();
		MutationType[] values = MutationType.values();
		for (MutationType type : values) {
			boolean enabled = d.enableMutationType(type);
			map.put(type, enabled);
		}
		return map;
	}

	public void setMutationType(MutationType t, boolean b) {
		typeEnabled.put(t, b);
	}
	
	@Override
	public boolean enableMutationType(MutationType t) {
		return typeEnabled.get(t);
	}

	@Override
	public int getTimeoutInSeconds() {
		return timeOut;
	}

	@Override
	public String getExcludedTests() {
		return excludedTests;
	}

	@Override
	public File getExcludeFile() {
		return excludedFile;
	}

	@Override
	public String getIgnorePattern() {
		return ignorePattern;
	}

	@Override
	public File getMutationIdFile() {
		return mutationIdFile;
	}

	@Override
	public File getOutputDir() {
		return outputDir;
	}

	@Override
	public String getProjectPrefix() {
		return projectPrefix;
	}

	@Override
	public String getProjectSourceDir() {
		return sourceDir;
	}

	@Override
	public RunMode getRunMode() {
		return runMode;
	}

	@Override
	public int getSaveInterval() {
		return saveIntervall;
	}

	@Override
	public File getTestExcludeFile() {
		return excludeFile;
	}



	@Override
	public String getTestNames() {
		return testNames;
	}

	@Override
	public int getTestPermutations() {
		return testPermutations;
	}


	@Override
	public boolean stopAfterFirstFail() {
		return stopAfterFirstFail;
	}

	@Override
	public boolean storeTestMessages() {
		return storeTestMessages;
	}

	@Override
	public boolean storeTraces() {
		return storeTraces;
	}

	@Override
	public boolean useThreadStop() {
		return useThreadStop;
	}

	public void setExcludedTests(String excludedTests) {
		this.excludedTests = excludedTests;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setExcludedFile(File excludedFile) {
		this.excludedFile = excludedFile;
	}

	public void setIgnorePattern(String ignorePattern) {
		this.ignorePattern = ignorePattern;
	}

	public void setMutationIdFile(File mutationIdFile) {
		this.mutationIdFile = mutationIdFile;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}

	public void setSaveIntervall(int saveIntervall) {
		this.saveIntervall = saveIntervall;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public void setExcludeFile(File excludeFile) {
		this.excludeFile = excludeFile;
	}


	public void setTestNames(String testNames) {
		this.testNames = testNames;
	}

	public void setTestPermutations(int testPermutations) {
		this.testPermutations = testPermutations;
	}

	public void setStopAfterFirstFail(boolean stopAfterFirstFail) {
		this.stopAfterFirstFail = stopAfterFirstFail;
	}

	public void setStoreTestMessages(boolean storeTestMessages) {
		this.storeTestMessages = storeTestMessages;
	}

	public void setStoreTraces(boolean storeTraces) {
		this.storeTraces = storeTraces;
	}

	public void setUseThreadStop(boolean useThreadStop) {
		this.useThreadStop = useThreadStop;
	}

	@Override
	public boolean runAllTestsForMutation() {
		return runAllTestsForMutation;
	}

	public void setRunAllTestsForMutation(boolean runAllTestsForMutation) {
		this.runAllTestsForMutation = runAllTestsForMutation;
	}

	@Override
	public boolean useJunit3Runner() {
		return this.useJunit3Runner;
	}

	public boolean isUseJunit3Runner() {
		return useJunit3Runner;
	}

	public void setUseJunit3Runner(boolean useJunit3Runner) {
		this.useJunit3Runner = useJunit3Runner;
	}

}
