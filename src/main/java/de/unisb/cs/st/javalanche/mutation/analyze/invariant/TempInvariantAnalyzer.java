package de.unisb.cs.st.javalanche.mutation.analyze.invariant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.mutation.analyze.AnalyzeUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners.InvariantUtils;

public class TempInvariantAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger
			.getLogger(TempInvariantAnalyzer.class);
	private Set<Integer> allViolated;
	private Map<String, Integer> missedMap = new HashMap<String, Integer>();

	private static final Map<Long, Map<String, Set<Integer>>> allAdd = new HashMap<Long, Map<String, Set<Integer>>>();
	private static final Map<Long, Map<String, Set<Integer>>> allMissed = new HashMap<Long, Map<String, Set<Integer>>>();

	// static{
	// loadAll();
	// System.exit(0);
	// }
	private static void loadAll() {
		StopWatch stp = new StopWatch();
		stp.start();
		List<Long> ids = getIds();
		int count = 0;
		for (Long id : ids) {
			count++;

			Map<String, Set<Integer>> addInvariants = getAddInvariants(id);
			Map<String, Set<Integer>> missedInvariants = getMissedInvariants(id);
			allAdd.put(id, addInvariants);
			allMissed.put(id, missedInvariants);
		}
		logger.info("Reading files too "
				+ DurationFormatUtils.formatDurationHMS(stp.getTime()));
	}

	private static List<Long> getIds() {
		File dir = new File("invariant-files/");
		String[] list = dir.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.startsWith("add-")) {
					return true;
				}
				return false;
			}

		});
		List<Long> ids = new ArrayList<Long>();
		for (String string : list) {
			String substring = string.substring(string.indexOf("add-") + 4,
					string.length());
			ids.add(Long.valueOf(substring));
		}
		return ids;
	}

	public String analyze(Iterable<Mutation> mutations) {
		int mutationsWithResult = 0;
		int killed = 0;
		int killedAndDifference = 0;
		int killedAndTotalDifference = 0;
		int killedAndAddDifference = 0;
		int difference = 0;
		int totalDifference = 0;
		int addDifference = 0;
		for (Mutation mutation : mutations) {
			if (mutation.getMutationResult() != null) {
				mutationsWithResult++;
//				if(mutationsWithResult >100){
//					break;
//				}
				Long id = mutation.getId();
				Map<String, Set<Integer>> addInvariants = getAddInvariants(id);
				Map<String, Set<Integer>> missedInvariants = getMissedInvariants(id);
				int addsize = getSize(addInvariants);
				int missedSize = getSize(missedInvariants);
				boolean violatedSpecific = addsize + missedSize > 0;
				boolean violatedAdd = addsize > 0;
				boolean violatedTotal = violateTotalInvariants(addInvariants);
				if (mutation.isKilled()) {
					killed++;
					killedAndDifference += violatedSpecific ? 1 : 0;
					killedAndTotalDifference += violatedTotal ? 1 : 0;
					killedAndAddDifference += violatedAdd ? 1 : 0;

				}
				difference += violatedSpecific ? 1 : 0;
				totalDifference += violatedTotal ? 1 : 0;
				addDifference += violatedAdd ? 1 : 0;

			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Mutations with results %d \n",
				mutationsWithResult));
		sb.append(String.format("Detected Mutations %d \n", killed));
		sb.append(String.format(
				"Mutations that violate invariants per test:  %d \n",
				difference));
		sb.append(String.format("Mutations that violate invariants:  %d \n",
				totalDifference));
		sb.append("\nResults for differing violations per test:\n");
		sb.append(getText(mutationsWithResult, killed, killedAndDifference, difference));
		sb.append("\nResults for additional violations per test:\n");
		sb.append(getText(mutationsWithResult, killed, killedAndAddDifference,
				addDifference));
		sb.append("\nResults for total violations per test:\n");
		sb.append(getText(mutationsWithResult, killed, killedAndTotalDifference,
				totalDifference));

		List<Entry<String, Integer>> l = new ArrayList<Entry<String, Integer>>(
				missedMap.entrySet());
		Collections.sort(l, new Comparator<Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		});
		for (Entry<String, Integer> entry : l) {
			System.out.println(entry.getKey() + "   " + entry.getValue());
		}
		return sb.toString();
	}

	private String getText(int mutationsWithResult, int killed,
			int killedAndDifference, int difference) {
		StringBuilder sb = new StringBuilder();
		sb
				.append(String
						.format(
								"Detected Mutations with difference:  %d   / %s relative of detected mutations / %s of mutations with differences\n",
								killedAndDifference, AnalyzeUtil.formatPercent(
										killedAndDifference, killed),
								AnalyzeUtil.formatPercent(killedAndDifference,
										difference)));

		sb
				.append(String
						.format(
								"Detected Mutations:      %5d     %6s difference     %6s no difference\n",
								killed, AnalyzeUtil.formatPercent(
										killedAndDifference, killed),
								AnalyzeUtil.formatPercent(killed
										- killedAndDifference, killed)));

		int notDetected = mutationsWithResult - killed;
		sb
				.append(String
						.format(
								"Not detected mutations:  %5d     %6s difference     %6s no difference\n",
								notDetected, AnalyzeUtil.formatPercent(
										difference - killedAndDifference,
										notDetected),
								AnalyzeUtil.formatPercent(notDetected
										- (difference - killedAndDifference),
										notDetected)));
		return sb.toString();
	}

	private boolean violateTotalInvariants(
			Map<String, Set<Integer>> addInvariants) {
		Set<Integer> totalInvariants = getTotal();
		for (Set<Integer> s : addInvariants.values()) {
			for (Integer i : s) {
				if (!totalInvariants.contains(i)) {
					return true;
				}
			}
		}
		return false;
	}

	private Set<Integer> getTotal() {
		if (allViolated == null) {
			allViolated = InvariantUtils.getAllViolated();
		}
		return allViolated;
	}

	private int getSize(Map<String, Set<Integer>> map) {
		int totalSize = 0;
		Set<Entry<String, Set<Integer>>> entrySet = map.entrySet();
		for (Entry<String, Set<Integer>> entry : entrySet) {
			totalSize += entry.getValue().size();
			String key = entry.getKey();
			if (entry.getValue().size() > 0) {
				if (missedMap.containsKey(key)) {
					missedMap.put(key, missedMap.get(key) + 1);
				} else {
					missedMap.put(key, 1);
				}
			}
		}
		return totalSize;
	}

	private static Map<String, Set<Integer>> getMissedInvariants(Long id) {
		File file = new File("invariant-files/missed" + "-" + id);
		Map<String, Set<Integer>> map = SerializeIo.get(file);
		return map;
	}

	private static Map<String, Set<Integer>> getAddInvariants(Long id) {
		File file = new File("invariant-files/add" + "-" + id);
		Map<String, Set<Integer>> map = SerializeIo.get(file);
		return map;
	}
}
