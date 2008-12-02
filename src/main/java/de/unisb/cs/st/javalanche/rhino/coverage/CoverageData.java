package de.unisb.cs.st.javalanche.rhino.coverage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CoverageData {

	private List<ClassData> classDataList = new ArrayList<ClassData>();

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

	public static List<CoverageData> prioritize(List<CoverageData> data) {
		Collections.sort(data, new Comparator<CoverageData>() {

			public int compare(CoverageData o1, CoverageData o2) {
				int i1 = o1.getNumberOfCoveredLines();
				int i2 = o2.getNumberOfCoveredLines();
				return i1 - i2;
			}

		});
		return data;
	}
}
