package org.softevo.mutation.objectInspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodVariables {

	private final List<VariableInfo> variableInfo;

	private Map<String, VariableInfoAndObject> variableMap;

	public MethodVariables(List<VariableInfo> variableInfo) {
		this.variableInfo = variableInfo;
		intializeVariableMap();
	}

	private void intializeVariableMap() {
		variableMap = new HashMap<String, VariableInfoAndObject>();
		for (VariableInfo vi : variableInfo) {
			variableMap.put(vi.getName(), new VariableInfoAndObject(vi));
		}
	}

	public List<VariableInfoAndObject> getVariables() {
		List<VariableInfoAndObject> vars = new ArrayList<VariableInfoAndObject>();
		for (VariableInfoAndObject vi : variableMap.values()) {
			vars.add(vi);
		}
		return vars;
	}

	public boolean hasVariable(String variableName) {
		return variableMap.containsKey(variableName);
	}

	public VariableInfoAndObject getVariable(String name) {
		VariableInfoAndObject result = null;
		if (hasVariable(name)) {
			result = variableMap.get(name);
		}
		return result;
	}
	
	

}
