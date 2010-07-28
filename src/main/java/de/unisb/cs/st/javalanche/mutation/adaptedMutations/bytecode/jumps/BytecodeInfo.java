package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class BytecodeInfo {

	private Map<String, Integer> data = new HashMap<String, Integer>();
	private Multimap<String, VariableInfo> varMap = HashMultimap.create();

	public int getLastLine(String className, String methodName, String desc) {
		String key = getKey(className, methodName, desc);
		if (data.containsKey(key)) {
			Integer res = data.get(key);
			return res;
		} else {
			throw new RuntimeException("Got no data for methods " + key);
		}

	}

	public void addLastLine(String className, String methodName, String desc,
			int line) {
		String key = getKey(className, methodName, desc);
		if (data.containsKey(key)) {
			throw new RuntimeException("Key already contained " + key);
		}
		data.put(key, line);

	}

	private String getKey(String className, String methodName, String desc) {
		return className + "." + methodName + desc;
	}

	public void write() {
		XmlIo.toXML(this, MutationProperties.LAST_LINE_INFO_FILE);
	}

	public static BytecodeInfo read() {
		if (MutationProperties.LAST_LINE_INFO_FILE != null
				&& new File(MutationProperties.LAST_LINE_INFO_FILE).exists()) {
			return (BytecodeInfo) XmlIo
					.fromXml(MutationProperties.LAST_LINE_INFO_FILE);
		} else {
			return null;
		}
	}

	public void addLocalVar(String className, String methodName, String desc,
			String vname, String vdesc, Label start, Label end, int index) {
		String key = getKey(className, methodName, desc);
		Type type = Type.getType(vdesc);
		// int size = type.getSize();
		VariableInfo variableInfo = new VariableInfo(index, type);
		varMap.put(key, variableInfo);
	}

	public Collection<VariableInfo> getLocalVars(String className,
			String methodName, String desc) {
		String key = getKey(className, methodName, desc);
		Collection<VariableInfo> result = varMap.get(key);
		return result;
	}
}
