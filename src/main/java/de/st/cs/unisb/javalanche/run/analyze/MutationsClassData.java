package de.st.cs.unisb.javalanche.run.analyze;

import java.io.File;
import java.util.Map;

import de.st.cs.unisb.ds.util.io.XmlIo;

public class MutationsClassData {

	private int mutationsKilled;

	private int mutationsSurvived;

	private int mutationsTotal;

	private String className;

	public MutationsClassData(String className) {
		this.className = className;
	}

	private void addMutationTotal() {
		mutationsTotal++;
	}

	public void addMutationKilled() {
		addMutationTotal();
		mutationsKilled++;
	}

	public void addMutationSurvived() {
		addMutationTotal();
		mutationsSurvived++;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the mutationsKilled
	 */
	public int getMutationsKilled() {
		return mutationsKilled;
	}

	/**
	 * @return the mutationsSurvived
	 */
	public int getMutationsSurvived() {
		return mutationsSurvived;
	}

	/**
	 * @return the mutationsTotal
	 */
	public int getMutationsTotal() {
		return mutationsTotal;
	}



	@SuppressWarnings("unchecked")
	public static Map<String, MutationsClassData> getMapFromFile(File file){
		Map<String, MutationsClassData> resultMap =  (Map<String, MutationsClassData>) XmlIo.fromXml(file);
		return resultMap;
	}

	/**
	 * @param mutationsTotal the mutationsTotal to set
	 */
	public void setMutationsTotal(int mutationsTotal) {
		this.mutationsTotal = mutationsTotal;
	}

}