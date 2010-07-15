package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import java.io.File;
import java.util.Map;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class JumpInfo {

	private static Map<Long, Integer> targetMap = null;
	private static Map<Long, Integer> endMap = null;

	@SuppressWarnings("unchecked")
	private static Map<Long, Integer> getTargetMap() {
		if (targetMap == null) {
			File f = new File(MutationProperties.ADAPTED_TARGET_FILE_NAME);
			targetMap = (Map<Long, Integer>) XmlIo.fromXml(f);
		}
		return targetMap;
	}

	@SuppressWarnings("unchecked")
	private static Map<Long, Integer> getEndMap() {
		if (endMap == null) {
			File f = new File(MutationProperties.ADAPTED_END_FILE_NAME);
			endMap = (Map<Long, Integer>) XmlIo.fromXml(f);
		}
		return endMap;
	}

	public static int getTargetLine(Mutation mutation) {
		return getTargetMap().get(mutation.getId());
	}

	public static int getEndLine(Mutation mutation) {
		return getEndMap().get(mutation.getId());
	}

	public static void writeMap(Map<Long, Integer> jumpMap) {
		JumpInfo.targetMap = null;
		File f = new File(MutationProperties.ADAPTED_TARGET_FILE_NAME);
		XmlIo.toXML(jumpMap, f);
	}

	public static void writeEndMap(Map<Long, Integer> endMap) {
		JumpInfo.endMap = null;
		File f = new File(MutationProperties.ADAPTED_END_FILE_NAME);
		XmlIo.toXML(endMap, f);
	}

}
