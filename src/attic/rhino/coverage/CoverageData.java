package de.unisb.cs.st.javalanche.rhino.coverage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoverageData {

	private List<ClassData> classDataList = new ArrayList<ClassData>();

	private String testName;

	private Set<SetEntry> set;

	public void add(ClassData classData) {
		classDataList.add(classData);
	}

	public int getNumberOfCoveredLines() {
		int sum = 0;
		for (ClassData cd : classDataList) {
			sum += cd.getNumberOfCoveredLines();
		}
		return sum;
	}

	public Set<SetEntry> getSetRepresentation() {
		if (set == null) {
			set = createSetRepresentation();
		}
		return set;
	}

	private Set<SetEntry> createSetRepresentation() {
		Set<SetEntry> set = new HashSet<SetEntry>();

		for (ClassData classData : classDataList) {
			String className = classData.getClassName();
			List<Integer> coveredLines = classData.getCoveredLines();
			for (Integer lineNumber : coveredLines) {
				set.add(new SetEntry(className, lineNumber));
			}
		}
		return set;
	}

	public static List<PriorizationResult> prioritizeTotal(
			List<CoverageData> data) {
		Collections.sort(data, new Comparator<CoverageData>() {

			public int compare(CoverageData o1, CoverageData o2) {
				int i1 = o1.getNumberOfCoveredLines();
				int i2 = o2.getNumberOfCoveredLines();
				return i1 - i2;
			}

		});
		List<PriorizationResult> prioritizedList = new ArrayList<PriorizationResult>();
		for (CoverageData coverageData : data) {
			prioritizedList.add(new PriorizationResult(coverageData
					.getTestName(), "covered "
					+ coverageData.getNumberOfCoveredLines() + " lines"));
		}
		return prioritizedList;
	}

	public static List<PriorizationResult> prioritizeAdditional(
			List<CoverageData> data) {
		Set<CoverageDataSet> coverageDataSet = new HashSet<CoverageDataSet>();
		for (CoverageData coverageData : data) {
			Set<SetEntry> setRepresentation = coverageData
					.getSetRepresentation();
			coverageDataSet.add(new CoverageDataSet(setRepresentation,
					coverageData.toString(), coverageData));
		}
		Set<SetEntry> usedSet = new HashSet<SetEntry>();
		List<PriorizationResult> prioritizedList = new ArrayList<PriorizationResult>();
		while (coverageDataSet.size() > 0) {
			CoverageDataSet nextEntry = getNextEntry(coverageDataSet, usedSet);
			coverageDataSet.remove(nextEntry);
			usedSet.addAll(nextEntry.set);
			prioritizedList.add(new PriorizationResult(nextEntry
					.getCoverageData().getTestName(), "Additional lines "
					+ nextEntry.size()));
		}
		Collections.reverse(prioritizedList);
		return prioritizedList;
	}

	private static CoverageDataSet getNextEntry(
			Set<CoverageDataSet> coverageDataSet, Set<SetEntry> usedSet) {
		int maxScore = Integer.MIN_VALUE;
		CoverageDataSet nextEntry = null;
		for (CoverageDataSet setEntry : coverageDataSet) {
			setEntry.removeAll(usedSet);
			if (setEntry.size() > maxScore) {
				maxScore = setEntry.size();
				nextEntry = setEntry;
			}
		}
		return nextEntry;
	}

	/**
	 * @author David Schuler
	 *
	 */
	public static class CoverageDataSet {

		CoverageData coverageData;

		Set<SetEntry> set;

		String name;

		public CoverageDataSet(Set<SetEntry> set, String name,
				CoverageData coverageData) {
			super();
			this.set = set;
			this.name = name;
			this.coverageData = coverageData;
		}

		public int size() {
			return set.size();
		}

		public void removeAll(Set<SetEntry> usedSet) {
			set.removeAll(usedSet);
		}

		/**
		 * @return the coverageData
		 */
		public CoverageData getCoverageData() {
			return coverageData;
		}

	}

	public static class SetEntry {
		String name;

		int lineNumber;

		public SetEntry(String name, int lineNumber) {
			super();
			this.name = name;
			this.lineNumber = lineNumber;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + lineNumber;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SetEntry other = (SetEntry) obj;
			if (lineNumber != other.lineNumber)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	/**
	 * @return the testName
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @param testName
	 *            the testName to set
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}
}
