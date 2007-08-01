package org.softevo.mutation.mutationPossibilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;

public class Mutations implements Iterable<MutationPossibility> {

	private Map<String, Map<Integer, List<MutationPossibility>>> map = new HashMap<String, Map<Integer, List<MutationPossibility>>>();

	private List<MutationPossibility> muationList;

	public Mutations(List<MutationPossibility> possibilities) {
		super();
		this.muationList = possibilities;
		for (MutationPossibility mp : possibilities) {
			Map<Integer, List<MutationPossibility>> classPossibilityMap = map
					.get(mp.getClassName());
			if (classPossibilityMap == null) {
				classPossibilityMap = new HashMap<Integer, List<MutationPossibility>>();
				map.put(mp.getClassName(), classPossibilityMap);
			}
			List<MutationPossibility> lineList = classPossibilityMap.get(mp
					.getLineNumber());
			if (lineList == null) {
				lineList = new ArrayList<MutationPossibility>();
				classPossibilityMap.put(mp.getLineNumber(), lineList);
			}
			lineList.add(mp);
		}
	}

	public boolean contains(String className, int lineNumber) {
		if(className.contains("/")){
			throw new IllegalArgumentException("class name contains / "+ className);
		}
		if (map.containsKey(className)) {
			Map<Integer, List<MutationPossibility>> classPossibilityMap = map
					.get(className);
			if (classPossibilityMap.containsKey(lineNumber)) {
				return true;
			}
		}
		return false;
	}

	public MutationPossibility get(String className, int lineNumber) {
		if(className.contains("/")){
			throw new IllegalArgumentException("class name contains / "+ className);
		}
		if (map.containsKey(className)) {
			Map<Integer, List<MutationPossibility>> classPossibilityMap = map
					.get(className);
			if (classPossibilityMap.containsKey(lineNumber)) {
				return classPossibilityMap.get(lineNumber).get(0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Mutations fromXML() {
		return new Mutations((List<MutationPossibility>) XmlIo
				.fromXml(new File(MutationProperties.MUTATIONS_TO_APPLY_FILE)));
	}

	public boolean containsClass(String className) {
		return map.containsKey(className);
	}

	public Iterator<MutationPossibility> iterator() {
		return muationList.iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String className : map.keySet()) {
			sb.append("Mutations for class" + className);
			sb.append('\n');
			Map<Integer, List<MutationPossibility>> tempMap = map
					.get(className);
			for (Integer i : tempMap.keySet()) {
				sb.append("in line: " + i);
				sb.append('\n');
				for (MutationPossibility mp : tempMap.get(i)) {
					sb.append(mp.toString());
					sb.append('\n');
				}
			}
		}
		return sb.toString();
	}

}
