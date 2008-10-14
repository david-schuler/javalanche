/**
 *
 */
package de.unisb.cs.st.javalanche.mutation.objectInspector;

public class VariableInfo {
	protected final String name;

	protected final String desc;

	protected final int index;

	protected final String fileName;

	public VariableInfo(String name, String desc, int index, String fileName) {
		super();
		this.name = name;
		this.desc = desc;
		this.index = index;
		this.fileName = fileName;
	}

	public VariableInfo(VariableInfo variableInfo) {
		this.name = variableInfo.name;
		this.desc = variableInfo.desc;
		this.index = variableInfo.index;
		this.fileName = variableInfo.fileName;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
