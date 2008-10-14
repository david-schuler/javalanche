package de.unisb.cs.st.javalanche.mutation.objectInspector;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class VariableInfoAndObject extends VariableInfo {

	private Object object;

	public VariableInfoAndObject(VariableInfo variableInfo) {
		super(variableInfo);
		object = XmlIo.fromXml(variableInfo.getFileName());
	}

	public Object getObject() {
		return object;
	}

}
