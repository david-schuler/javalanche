/**
 * 
 */
package de.unisb.cs.st.javalanche.mutation.objectInspector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Variables {

	private Map<String, List<VariableInfo>> variableInfo;

	public Variables(Map<String, List<VariableInfo>> variableInfo) {
		this.variableInfo = variableInfo;
	}

	public MethodVariables getMethodVariables(String method) {
		return new MethodVariables(variableInfo.get(method));
	}

	public Set<String> getMethodNames() {
		return Collections.unmodifiableSet(variableInfo.keySet());
	}

}
