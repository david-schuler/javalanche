package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

public class VariableInfo {

	private String name;

	private String desc;

	public VariableInfo(String name, String desc) {
		super();
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

}
