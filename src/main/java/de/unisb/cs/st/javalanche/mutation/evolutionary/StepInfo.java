package de.unisb.cs.st.javalanche.mutation.evolutionary;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * @author David Schuler
 * 
 */
public class StepInfo {

	private static final String DEFAULT_LOCATION = MutationProperties.OUTPUT_DIR
			+ "/stepInfo.txt";
	private Map<Long, String> info;

	StepInfo(Map<Long, String> info) {
		super();
		this.info = info;
	}

	public String getInfo(Long l) {
		return info.get(l);
	}

	public void writeToDefaultLocation() {
		Set<Entry<Long, String>> entrySet = info.entrySet();
		StringBuilder content = new StringBuilder();
		for (Entry<Long, String> entry : entrySet) {
			content.append(entry.getKey());
			content.append(',');
			content.append(entry.getValue());
			content.append('\n');
		}
		Io.writeFile(content.toString(), new File(DEFAULT_LOCATION));
	}

	public static StepInfo getFromDefaultLocation() {
		File f = new File(DEFAULT_LOCATION);
		return readFromFile(f);
	}

	private static StepInfo readFromFile(File f) {
		Map<Long, String> info = new HashMap<Long, String>();
		List<String> linesFromFile = Io.getLinesFromFile(f);
		for (String line : linesFromFile) {
			String[] split = line.split(",");
			Long id = Long.valueOf(split[0]);
			String mutationIinfo = split[1];
			info.put(id, mutationIinfo);
		}
		return new StepInfo(info);
	}
}
