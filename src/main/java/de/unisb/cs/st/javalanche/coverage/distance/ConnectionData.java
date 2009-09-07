package de.unisb.cs.st.javalanche.coverage.distance;

import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class ConnectionData {

	Set<Tuple> connections = new HashSet<Tuple>();

	public void addConnection(String className1, String methodName1,
			String desc1, String className2, String methodName2, String desc2) {
		MethodDescription m1 = new MethodDescription(className1, methodName1,
				desc1);
		MethodDescription m2 = new MethodDescription(className2, methodName2,
				desc2);
		connections.add(new Tuple(m1, m2));
	}

	public Set<MethodDescription> getAllMethods() {
		Set<MethodDescription> result = new HashSet<MethodDescription>();
		for (Tuple t : connections) {
			result.add(t.getStart());
			result.add(t.getEnd());
		}
		return result;
	}

	public boolean hasConnection(MethodDescription m1, MethodDescription m2) {
		return connections.contains(new Tuple(m1, m2));
	}

	public void save() {
		XmlIo.toXML(this, MutationProperties.CONNECTION_DATA_FILE);
	}

	public static ConnectionData read() {
		return XmlIo.get(MutationProperties.CONNECTION_DATA_FILE);
	}

}
